package spore

//@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.2' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.ANY
import errors.MethodCallError

class Method {

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
			def prop= this.properties.find({it.key==k})
			if (this.properties.find({it.key==k && ![
					'nomdePorpPourLaquelleIlYaUnTraitementDifferent'
				].contains(k)})){
				this."$k"=v
			}
		}
	}
	def contentTypesNormalizer={
		
	}
	/**For the moment it's not quite apparent,
	 * but this spot is the most important part
	 * of the workflow. It's a property of the class
	 * and it's value is a closure. 
	 */
	def request={reqParams->

		def ret = ""
		def requiredParamsMissing=[]
		def whateverElseMissing=[]
		def errors=[]
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
			/**base_url,method,headers.Accept*/
			builder.request(base_url,method?.toUpperCase()?:GET,ANY) {
				uri.path = path
				println request.URI
				println uri.toString()
				uri.query = reqParams
				headers.'User-Agent' = 'Mozilla/5.0'
				//header.'Content-Type'
				//headers.'Accept'=formats
				//TODO bon ici il te faut un test sur le contentType
				response.success = { resp, reader ->
					/*resp.properties.each(){k,v->
						println k
						println v
						
					}*/
					String statusCode=String?.valueOf(resp.statusLine.statusCode)
					ret+=reader
					ret+=statusCode
				}
				/*c'est pour si c'est du json
				 response.success = { resp, json ->
				 //  assert json.size() == 3
				 println "Query response: "+json
				 println resp.responseData
				 //  json.responseData.results.each {
				 //	println "  ${it.titleNoFormatting} : ${it.visibleUrl}"
				 // }
				 }*/
				response.failure ={resp->
						String statusCode=String?.valueOf(resp.statusLine.statusCode)
						ret+="request failure"+statusCode
				}
			}
		}
		if (!requiredParamsMissing.empty){
			requiredParamsMissing.each{
				 ret += "$it is missing for $name" 
				 }
		}
		println ret
		return ret
	}
}
