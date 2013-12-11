package spore

class Middleware {

	def addHeaders(environ,headers){
		headers.each(){header->
			environ['spore.headers']=header
		}
	}
}
