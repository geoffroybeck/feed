package feed
import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

class IAmAWebServiceController {
	static allowedMethods = [vote:'POST']


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

	//def new(){
	//	println "queryString"+request?.queryString
	//	return render(params)
	//}




	def vote() {
		def slurper = new JsonSlurper()
		//println request.reader.getText()
		println "User agent: " + request.getHeader("User-Agent")
		println "queryString"+request?.queryString
		println "JSON"+request?.JSON
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
		def queryOutputMapJson= m as grails.converters.JSON
		 queryOutputMapJson.toString()
		return render(queryOutputMapJson)
	}

}
