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
import java.net.*;

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
	}
	def contentTypesNormalizer(){
		String normalized
		def format=formats?:global_formats
		normalized=contentTypes[format.class==java.lang.String?format.toUpperCase():format[0].toUpperCase()]
	}
	
	def baseEnviron(){
		return [
			'REQUEST_METHOD': method,
			'SERVER_NAME': urlParse().hostname,
			'SERVER_PORT': urlParse().serverport,
			//'SCRIPT_NAME': script_name(parsed_base_url.path),
			//'PATH_INFO': path_info,
			'QUERY_STRING': "",
			'HTTP_USER_AGENT': 'whatever',
			'spore.expected_status': expected_status,
			'spore.authentication': authentication,
			'spore.params': '',
			'spore.payload': '',
			'spore.errors': '',
			'spore.format': formats,
			'spore.userinfo': userinfo(parsed_base_url),
			'wsgi.url_scheme': parsed_base_url.scheme,
			]
	}
	/**Right now, it's not quite apparent,
	 * but this spot is the most important part
	 * of the workflow. It's a property of the class
	 * and it's value is a closure.
	 */
	def request={reqParams->
		urlParse()
		Map queryString=reqParams
		def ret = ""
		String dynamicPathComponent=""
		String staticPathComponent=path
		def requiredParamsMissing=[]
		def whateverElseMissing=[]
		def errors=[]//parsed_base_url.hostname

		//right now search and replace one placeholder
		if (path.indexOf(':')!=-1){
			dynamicPathComponent=path.substring(path.indexOf(':')+1,path.length())
			staticPathComponent=path.replace(":"+dynamicPathComponent,"")

		}
		//round brackets mandatory here
		String finalPath=path
		def entry= reqParams.find({k,v->k==dynamicPathComponent})
		println "ENTRY"+entry
		if (entry!=null){
			finalPath=staticPathComponent+entry.value
			queryString.remove(entry.key)
			
		}
		//String finalPath=staticPathComponent+(reqParams.find({k,v->k==dynamicPathComponent})?.value?:"")
		//reqParams.find({k,v->k==dynamicPathComponent})?reqParams=reqParams.remove(dynamicPathComponent):""
		println "name : $name, required_params : ${required_params?:'none'}, effective params on call : ${reqParams?reqParams:''}"
		required_params.each{
			if (!reqParams.find({k,v->k==it})){
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
			println "????"+queryString
			println "????"+path
			/**base_url,method,headers.Accept*/
			println base_url
			//Accept c'est une contrainte sur ce qu'on est disposés à recevoir dans la réponse
			//si formats  c'est une spécification de ce qu'est ok de revoir le web service
			//alors c'est contentType qui doit être généré en fonction de formats
			builder.request(base_url,POST,contentTypesNormalizer()) {
				//bon là ça va
				uri.path = finalPath
				uri.query = queryString
				headers.'User-Agent' = 'Satanux/5.0'
				headers.Accept=contentTypesNormalizer()
				//headers.'Content-Type'=JSON
				//request.setContentType()
				if (request.method=="POST"){
					send JSON, ["clef":["subclef":'valeur']]
					// bon dans le httpBuilder de groovy, le body est là : request.entity.getContent()
				}else{

				}
				//bon en fait on dirait qu'il n'y a pas besoin de faire de
				//la conditionnalité ici.
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
					ret+="request failure"+statusCode
				}
			}
		}
		if (!requiredParamsMissing.empty){
			requiredParamsMissing.each{ ret += "$it is missing for $name"  }
		}
		return ret
	}
	Map urlParse(){
		URL aURL = new URL(base_url);
	//println("protocol = " + aURL.getProtocol());
	//println("authority = " + aURL.getAuthority());
	println("host = " + aURL.getHost());
	println("port = " + aURL.getPort());
	println("path = " + aURL.getPath());
	println("query = " + aURL.getQuery());
	println("filename = " + aURL.getFile());
	println("ref = " + aURL.getRef());
		return [
			"hostname":aURL.getHost(),
			"serverPort":aURL.getPort(),
			"aeaze":"zeaaze"
			]
	}
}
