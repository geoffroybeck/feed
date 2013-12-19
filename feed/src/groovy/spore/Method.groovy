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
			//def prop= this.properties.find({it.key==k})
			if (this.properties.find({
				it.key==k && ![
					'nomdePorpPourLaquelleIlYaUnTraitementDifferent'
				].contains(k)})){
				this."$k"=v
			}
		}
		//	println baseEnviron()
	}
	
	def contentTypesNormalizer(){
		def normalized
		def format=formats?:global_formats
		normalized=contentTypes[format.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]
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
	def getCurrentMiddlewares(){
		middlewares
	}
	/**Builds the actual request from parameters
	 * and environ
	 */
	def request={reqParams->
		
		println "middlewares"+delegate.middlewares
		Map environ = baseEnviron()
		def ret = ""
		
		def (requiredParamsMissing,whateverElseMissing,errors)=[[], [], []]

		environ['spore.params']=buildParams(reqParams)
		environ['spore.payload']=buildPayload(reqParams)
	
		

		def finalPath = placeHoldersReplacer(reqParams).finalPath
		def queryString = placeHoldersReplacer(reqParams).queryString
		
		

		//String finalPath=staticPathComponent+(reqParams.find({k,v->k==dynamicPathComponent})?.value?:"")
		//reqParams.find({k,v->k==dynamicPathComponent})?reqParams=reqParams.remove(dynamicPathComponent):""
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

			
			/**base_url,method,headers.Accept*/
			builder.request(base_url,POST,contentTypesNormalizer()) {
				//bon y'a moyen qu'ici les choses aient été faites en double
				uri.path = finalPath
				uri.query = queryString
				headers.'User-Agent' = 'Satanux/5.0'
				headers.Accept=contentTypesNormalizer()
				if (["POST", "PUT"].contains(request.method)){
					send contentTypesNormalizer(),environ['spore.payload'] 
					// bon dans le httpBuilder de groovy, le body est là : request.entity.getContent()
				}else{

				}
				//conditionnalité?
				if (headers.Accept==JSON){
					response.success = { resp, json ->
						String statusCode=String?.valueOf(resp.statusLine.statusCode)
						ret+=json
						ret+=" : "
						ret+=statusCode
					}
				}else{
					response.success = { resp, reader ->
						String statusCode=String?.valueOf(resp.statusLine.statusCode)
						ret+=reader
						ret+=" : "
						ret+=statusCode
					}
				}
				response.failure ={resp->
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret+="request failure"+" : "+statusCode
				}
			}
		}
		if (!requiredParamsMissing.empty){
			requiredParamsMissing.each{
				 ret += "$it is missing for $name"
				 environ["spore.errors"]?environ["spore.errors"]+="$it is missing for $name":(environ["spore.errors"]="$it is missing for $name")
				   }
		}
		return [ret:ret,environ:environ]
	}
	def mergeEnvironsAndParams(e,p){
		
	}
	def placeHoldersReplacer(req){
		Map queryString = req
		String dynamicPathComponent=""
		String staticPathComponent=path
		String corrected=""
		if (path.indexOf(':')!=-1){
			def split = path.split ('/').collect{it.startsWith(":")?req.find({k,v->k==it-(":")})?.value:it}.join('/')
			corrected = split
			//dynamicPathComponent=path.substring(path.indexOf(':')+1,path.length())
			//staticPathComponent=path.replace(":"+dynamicPathComponent,"")
		}
		String finalPath=path
		def toBeRemovedFromFinalQueryString=path.split ('/').findAll{it.startsWith(":")}.collect{
			it.replace(':','')
		}
		println toBeRemovedFromFinalQueryString
		toBeRemovedFromFinalQueryString.each{
			queryString.remove(it)
		}
		println queryString
		//def entry= req.find({k,v->k==dynamicPathComponent})
	//	if (entry!=null){
	//		finalPath=staticPathComponent+entry.value
	//		queryString.remove(entry.key)
	//	}
		
		return [queryString:queryString,finalPath:finalPath]
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
	 * @return only parameters that will replace placeholders
	 */
	def buildParams(p){
		return p.findAll{k,v->param(k) }
	}
	/**For each effective request parameter, checks if it is to be used
	 * to replace a placeholder.
	 */
	boolean param(param){
		List params=[]
		[optional_params,required_params].each(){
			if (it!=null && !it.empty && it!=""){
				params+=it
			}
		}
		param && param!="" && params.contains(param)
	}
}
