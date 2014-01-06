package spore

class Middleware {

	def addHeaders(environ,headers){
		headers.each(){header->
			//environ['spore.headers'] = header
		}
	}
	def call={params->
		println this.properties.findAll{k,v->
			println k
			println v
		}
		
		params.each {propName,propVal->
			this.metaClass."$propName"=propVal
		}
		//params['REQUEST_METHOD']="JAGUAR"
		if (this?.metaClass.methods*.name.contains('processRequest')){
			
			return true
		}else if (this?.metaClass.methods*.name.contains('processResponse')){
			
			def closure = this?.processResponse()
			
			return closure
		}else return null
	}
}
