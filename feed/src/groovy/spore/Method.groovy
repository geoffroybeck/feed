package spore

//@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.2' )
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import static groovyx.net.http.Method.PUT
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.TEXT
import static groovyx.net.http.ContentType.ANY

class Method {

	HTTPBuilder builder = new HTTPBuilder();
	def name
	def api_base_url
	//mandatory
	String method
	//mandatory
	def path
	def required_params
	def optional_params
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
	
	/*Method(args){
	
	}*/
	
	def request={reqParams->
		println "I'm being called, keep cool, with this name : $name"
		println "with this method : ${method?.toUpperCase()?:GET}"
		println "with this format : ${formats?:global_formats?:ANY}"
		println "and i'm being overloaded with those params ${reqParams?reqParams:''}"
		builder.request('http://www.google.com/search',method?.toUpperCase()?:GET,ANY) {
			//uri.path = ''
			uri.query = [ v:'1.0', q: 'Calvin and Hobbes' ]
			// bon ici il te faut un test sur le contentType
			response.success = { resp, reader ->
				assert resp.statusLine.statusCode == 200
				println resp.statusLine
				//println //reader // print response stream
			}
			/*ça c'est pour si c'est du json
			response.success = { resp, json ->
			//  assert json.size() == 3
			  println "Query response: "+json
			  println resp.responseData
			//  json.responseData.results.each {
			//	println "  ${it.titleNoFormatting} : ${it.visibleUrl}"
			// }
			}*/
			response.failure ={
				
			}
		}
	}
}
