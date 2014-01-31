package feed
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

class IAmAWebServiceController {
	static allowedMethods = [vote:'POST']
	def beforeInterceptor = [action: this.&auth, except: 'login']
	private auth() { 
		println params
		}
	 def by(){
		 Map m = [:]
		 params.findAll(){k,v->!['action', 'controller'].contains(k)}.each{k,v->
			 m[k]=v
		 }
		 def queryOutputMapJson= m as grails.converters.JSON
		 queryOutputMapJson.toString()
		 return render(queryOutputMapJson)
	 }
	def ask(){
		Map m = [:]
		params.findAll(){k,v->!['action', 'controller'].contains(k)}.each{k,v->
			m[k]=v
		}
		def queryOutputMapJson= m as grails.converters.JSON
		queryOutputMapJson.toString()
		return render(queryOutputMapJson)
	}
	def profile(){
		Map m = [:]
		params.findAll(){k,v->!['action', 'controller'].contains(k)}.each{k,v->
			m[k]=v
		}
		def queryOutputMapJson= m as grails.converters.JSON
		queryOutputMapJson.toString()
		return render(queryOutputMapJson)
	}
	def vote() {
		def slurper = new JsonSlurper()
		return render(request.JSON)
	}
	def login() {
		Map m = [:]
		params.findAll(){k,v->!['action', 'controller'].contains(k)}.each{k,v->
			m[k]=v
		}
		def queryOutputMapJson= m as grails.converters.JSON
		queryOutputMapJson.toString()
		return render(queryOutputMapJson)
	}

	def comment() {
		Map m = [:]
		params.findAll(){k,v->!['action', 'controller'].contains(k)}.each{k,v->
			m[k]=v
		}
		def queryOutputMapJson= m as grails.converters.JSON
		queryOutputMapJson.toString()
		return render(queryOutputMapJson)
	}
	def comments() {
		println "oh"
		request.properties.each{k,v->
			println k
			println v
		}

		request.headerNames.each{name->
			println "$name : "+request.getHeader(name)
			
		}
		Map m = [:]
		params.findAll(){k,v->!['action','controller'].contains(k)}.each{k,v->
			m[k]=v
		}
		def queryOutputMapJson= m as grails.converters.JSON
		 queryOutputMapJson.toString()
		return render(queryOutputMapJson)
	}
	 
	def page(){
	Map m = [:]
		params.findAll(){k,v->!['action','controller'].contains(k)}.each{k,v->
			m[k]=v
		}
		println  "PARAMS"+params
		def queryOutputMapJson= m as grails.converters.JSON
		 queryOutputMapJson.toString()
		return render(queryOutputMapJson)
	}
	def descriptionFile(){
		def slurper = new JsonSlurper()
		File file = new File("/home/geoffroy/Documents/workspace/feed/web-app/json/test.json")
		return  render ( slurper.parse(new FileReader("/home/geoffroy/Documents/workspace/feed/web-app/json/test.json"))as grails.converters.JSON)
	}

}
