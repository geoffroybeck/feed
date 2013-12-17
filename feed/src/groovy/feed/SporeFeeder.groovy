package feed

import groovy.json.JsonSlurper
import spore.Spore

class SporeFeeder {
	
	static slurper = new JsonSlurper()
	
	/**Depending whether the spore is being created from an url
	 * or from a file feed calls feedFromJson or feedFromUrl to
	 * slurp the Json into a Spore constructor
	 */
	def feed(spec_uri,base_url=null){
		
		def api_description = spec_uri.startsWith('http')?feedFromUrl(spec_uri):feedFromJson(spec_uri)
		if (!api_description["base_url"]){
			
			api_description["base_url"]=base_url
		}
		return new Spore(api_description)
	}
	private def feedFromJson(specs){
		 slurper.parse(new FileReader(specs))
	}
	private def feedFromUrl(specs){
		 slurper.parse(specs)
	}
}
