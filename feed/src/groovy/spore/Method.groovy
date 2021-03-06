package spore

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.HEAD
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.Method.DELETE
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.HTML
import errors.MethodCallError

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher
import java.util.regex.Pattern

import request.Response

class Method {
	static contentTypes = ['JSON':JSON,'TEXT':TEXT,'XML':XML,"HTML":HTML,"URLENC":URLENC,"BINARY":BINARY]
	//ça, ça peut pas vraiment marcher, il faut que tu choppes snapshot 0.7,
	//dedans il y a PATCH
	static methods = ["GET":GET,"POST":POST,"PUT":PUT,"PATCH":"PATCH"]
	
	HTTPBuilder builder = new HTTPBuilder();

	@Mandatory
	def name
	@Mandatory
	def api_base_url
	@Mandatory
	String method
	@Mandatory
	def path
	def required_params=[]
	def optional_params=[]
	def expected_status
	def required_payload
	def description
	def authentication
	def formats
	def base_url
	def documentation
	def middlewares
	def global_authentication
	def global_formats
	def defaults

	//Explicit  constructor
	Method(args){
		args?.each(){k,v->
			println "KKKKKK"+k
			if (this.properties.find({
				it.key==k})){
				this."$k"=v
			}
		}
	}

	def baseEnviron(){

		def normalizedPath=path.split ('/').collect{it.trim()}-null-""
		def formatedPathRemainder=path.replace('/'+(normalizedPath[0]),'')
		return [

			'REQUEST_METHOD': method,
			'SERVER_NAME': urlParse().hostName,
			'SERVER_PORT': urlParse().serverPort,
			'SCRIPT_NAME': normalizedPath.size()>0?('/'+(normalizedPath[0])):"",
			'PATH_INFO': formatedPathRemainder,
			'QUERY_STRING': "",
			'HTTP_USER_AGENT': 'whatever',
			'spore.expected_status': expected_status?:"",
			'spore.authentication': authentication,
			'spore.params': '',
			'spore.payload': '',
			'spore.errors': '',
			'spore.format': contentTypesNormalizer(),
			'spore.userinfo': urlParse().userInfo,
			'wsgi.url_scheme': urlParse().scheme

		]
	}

	/**Builds the actual request from 
	 * environ, parameters and enabled middlewares
	 * in that order
	 */
	def request={reqParams->
		
		boolean noRequest=false
		Map environ = baseEnviron()
		Map modifiedEnvirons = [:]
		Map middlewareModifiedEnviron=[:]
		def ret = ""
		def (requiredParamsMissing,whateverElseMissing,errors,storedCallbacks)=[[], [], [],[]]
		
		
		int i=0
		//ici il faut modifier les deux méthodes qui construisent
		//la requête effective.
		//les deux méthodes c'est placeHoldersreplacers
		//et  buildPayload
		def finalPath = placeHoldersReplacer(reqParams).finalPath
		def queryString = placeHoldersReplacer(reqParams).queryString
		
		environ['QUERY_STRING']=queryString
		environ['spore.params']=buildParams(reqParams)
		environ['spore.payload']=buildPayload(reqParams)
		/*rather not idiomatic breakable loop
		 * that call middlewares. Breaks if a response
		 * is found.
		 * */
		delegate.middlewares.find{condition,middleware->
			
			def callback
			
			//If the condition was written in Java
			if (condition.class == java.lang.reflect.Method){
				def declaringClass = condition.getDeclaringClass()
				//ici c'est pas super safe, ça part un peu de l'hypothèse 
				//que la declaringClass prend pour constructeur ça [:]
				//ceci dit il ne devrait pas y avoir de Middleware.condition 
				//qui ne soient pas comme ça.
				Object obj = declaringClass.newInstance([:])
				 if (condition.invoke(obj,environ)){
					 callback =	middleware.call(environ)
				 }
			}
			//else (i.e if it is a groovy.lang.Closure)
			else if (condition(environ)){
				
				callback =	middleware.call(environ)
				
			}
			
			
			/**break loop
			 */
			if (callback in Response){
				noRequest=true
				return true
				
			}
			
			/**store to process after request*/
			if (callback!=null){

				storedCallbacks+=callback
				
			}
			i++
			/**pass control to next middleware*/
			return false
		} 
		
		
		//From here environ is not modified anymore 
		//that's where missing 
		//or exceeding parameters
		//errors can be raised.
		required_params.each{
			if (!reqParams.containsKey(it) &&  ! environ['spore.params']){
				requiredParamsMissing+=it
			}
		}
		[
			requiredParamsMissing,
			whateverElseMissing
		].each() {
			!it.empty?errors+=it:''
		}
        // effective processing of the request
		 if (errors.size()==0 && noRequest==false){
                        //il faut voir pour remonter ça avec le reste
                        //parce tout ce qu'il y a là doit pouvoir être réécrit
                        environ['base_url']=base_url
                        environ['method']=method
                        environ['finalPath']=finalPath
                        environ['queryString']=queryString
                        ret = requestSend(environ)
                }
		if (!requiredParamsMissing.empty){
			requiredParamsMissing.each{
				ret += "$it is missing for $name"
				environ["spore.errors"]?environ["spore.errors"]+="$it is missing for $name":(environ["spore.errors"]="$it is missing for $name")
			}
		}
		return [ret:ret,environ:environ]//test purpose
	}

	def placeHoldersReplacer(req){
		Map queryString = req
		String corrected=""
		Map finalQuery=[:]
		if (path.indexOf(':')!=-1){
			corrected = path.split ('/').collect{it.startsWith(":")?req.find({k,v->k==it-(":")})?.value:it}.join('/')
		}
		def usedToBuildFinalPath=path.split ('/').findAll{it.startsWith(":")}.collect{
			it.replace(':','')
		}
		queryString.each{k,v->
			if (param(k) && ! usedToBuildFinalPath.contains(k)){
				finalQuery[k]=v
			}
		}
		return [queryString:finalQuery,finalPath:corrected!=""?corrected:path]
	}

	Map urlParse(){
		URL aURL = new URL(base_url)
		URI aURI = new URI(base_url)
		return [
			"hostName":aURL.getHost(),
			"serverPort":aURI.getPort(),
			"path":aURL.getPath(),
			"query" :aURL.getQuery(),
			"userInfo":aURL.getUserInfo(),
			"scheme":aURI.getScheme()
		]
	}

	/**pop ["payload"]from parameters and add payload to environ
	 * @param p : the request effective parameters
	 * @return the payload
	 */
	def buildPayload(p){
		def entry = p["payload"]
		p.remove("payload")
		return entry
	}

	/**Bon alors là mec t'as eu une interprétation charitable dans laquelle les exceeding paramaters 
	 * ne déclenchent pas d'erreurs et sont juste éliminés
	 * @param p : the request effective parameters
	 * @return only parameters that are listed under optional or required params
	 */
	def buildParams(p){
		return p.findAll{k,v->
			param(k)
		}
	}

	/**For each effective request parameter, checks if it is registered under 
	 * optional or required params
	 */
	boolean param(param){
		List params=[]
		[
			optional_params,
			required_params
		].each(){
			if (it!=null && !it.empty && it!=""){
				params+=it
			}
		}
		param && param!="" && params.contains(param)
	}
	def contentTypesNormalizer(){
		def normalized
		def format=formats?:global_formats
		normalized=contentTypes[format.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]
	}
	def contentTypesNormalizer(args){
		def normalized
		def format=args['formats']?:formats?:global_formats
		normalized=contentTypes[format.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]
	}
}
