package feed

class IAmAWebServiceController {

    def vote() {
		println "vote"
		println "what"+request?.queryString
		println "I'm required"+params
		return render([oui:"vote"])
	}
	def login() {
		println "login"
		println "what"+request?.queryString
		println "I'm required"+params
		return render([oui:"login"])
	}
	def ask(){
		println "ask"
		println "what"+request?.queryString
		println "I'm required"+params
		return render([oui:"ask"])
	}
	def comment() {
		println "comment"
		println "what"+request?.queryString
		println "I'm required"+params
		return render([oui:"comment"])
	}
	def comments() {
		println "comments"
		println "what"+request?.queryString
		println "I'm required"+params
		return render([oui:"comments"])
	}
	def profile(){
		println "profile"
		println "queryString"+request?.queryString
		println "I'm required"+params
		return render([oui:"profile"])
	}
	def page(){
		println "page"
		println "what"+request?.queryString
		println "I'm required"+params
		return render([oui:"page"])
	}
	
}
