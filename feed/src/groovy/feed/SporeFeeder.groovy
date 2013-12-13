package feed

import groovy.json.JsonSlurper
import spore.Spore
//import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH
//import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes as GA
//import org.codehaus.jackson.map.ObjectMapper;

class SporeFeeder {
	//def ctx = SCH.servletContext.getAttribute(GA.APPLICATION_CONTEXT)
	static slurper = new JsonSlurper()
	def feed(spec_uri,base_url=null){
		base_url=!base_url?"URL":base_url
		def api_description = spec_uri.startsWith('http')	?  feedFromUrl(spec_uri): feedFromJson(spec_uri)
		return new Spore(api_description)
	}
	private def feedFromJson(specs){
		 slurper.parse(new FileReader(specs))
	}
	private def feedFromUrl(specs){
		 slurper.parse(specs)
	}
}
