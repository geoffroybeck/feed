package spore

class Middleware {

	def addHeaders(environ,headers){
		
		headers.each(){header->
			environ['spore.headers'] = header
		}
	}
	def call={params->
		params.each {propName,propVal->
			this.metaClass."$propName"=propVal
		}
	}
}
