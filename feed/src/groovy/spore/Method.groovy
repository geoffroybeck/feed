package spore

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.ANY
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.ContentType.BINARY
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.HTML
import errors.MethodCallError
import java.util.regex.Matcher
import java.util.regex.Pattern

class Method {
	static contentTypes = ['JSON':JSON,'TEXT':TEXT,'XML':XML,"HTML":HTML,"URLENC":URLENC,"BINARY":BINARY]
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
			if (this.properties.find({
				it.key==k && ![
					'differentBehavior',
					'otherDifferentBehavior'
				].contains(k)})){
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



	/**Builds the actual request from parameters,
	 * environ, and enabled middlewares
	 */
	def request={reqParams->

		Map environ = baseEnviron()
		Map modifiedEnvirons = [:]
		Map middlewareModifiedEnviron=[:]
		List storedCallbacks=[]
		
		
		
		def ret = ""
		def (requiredParamsMissing,whateverElseMissing,errors)=[[], [], []]
		def finalPath = placeHoldersReplacer(reqParams).finalPath
		def queryString = placeHoldersReplacer(reqParams).queryString
		
		environ['QUERY_STRING']=queryString
		environ['spore.params']=buildParams(reqParams)
		environ['spore.payload']=buildPayload(reqParams)

		delegate.middlewares.find{condition,middleware->
			
			def callback
			
			if (condition(environ)){
				
				callback =	middleware.call(environ)
			}
			//bon  là c'est si tout est court-circuité
			if (callback==true){
				
				return true
				
			}
			//store to process after request
			if (callback!=null){

				storedCallbacks+=callback
				
			}
			//pass control to next middleware
			return false
		}
		
		//il doit y avoir une erreur de missingRequiredParams seulement si
		//et le merge des spore.params du middleware && les spore.params
		//du baseEnviron !contents(requiredParams)
		required_params.each{
			if (!reqParams.containsKey(it)){
				requiredParamsMissing+=it
			}
		}
		[
			requiredParamsMissing,
			whateverElseMissing
		].each() {
			!it.empty?errors+=it:''
		}

		if (errors.size()==0){

			builder.request(base_url,methods[method],contentTypesNormalizer()) {
				uri.path = finalPath
				uri.query = queryString
				headers.'User-Agent' = 'Satanux/5.0'
				headers.Accept=contentTypesNormalizer()

				if (["POST", "PUT", "PATCH"].contains(request.method)){
					send contentTypesNormalizer(),environ['spore.payload']
				}
				
				response.success =  {resp,json->
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret += json
					ret+=" : "
					ret+=statusCode
				}
				
				response.failure ={resp->
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret+="request failure"+" : "+statusCode
				}
			}
			
			
			def realRet
			storedCallbacks.reverseEach{
				realRet?realRet+=it():(realRet=it())
			}
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
			def split = path.split ('/').collect{it.startsWith(":")?req.find({k,v->k==it-(":")})?.value:it}.join('/')
			corrected = split
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

	def getCurrentMiddlewares(){
		middlewares
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

	/**
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
}
