package feed

class IAmAWebServiceController {
	static allowedMethods = [vote:'POST']
    def vote() {
		//println "what"+request?.queryString
		/*request.properties.each{propkey,propval->
			println "reqpropkey"+propkey
			//println "reqpropval"+propval
		}*/
		println "wwoooo"
		//ça marche
		println "????"+request.JSON
		request.properties.each{k,v->
			println "k"+ k
			println "v"+ v
		}
		//println request.reader.getText()
		println "woooo"
		println "User agent: " + request.getHeader("User-Agent")
		return render(params)
	}
	def login() {
		println "what"+request?.queryString
		println "User agent: " + request.getHeader("User-Agent")
		return render(params)
	}
	def ask(){
		println "what"+request?.queryString
		println "User agent: " + request.getHeader("User-Agent")
		return render(params)
	}
	def comment() {
		println "what"+request?.queryString
		return render(params)
	}
	def comments() {
		println "what"+request?.queryString
		return render(params)
	}
	def profile(){
		println "queryString"+request?.queryString
		return render(params)
	}
	def page(){
		println "what"+request?.queryString
		return render(params)
	}
	
}
