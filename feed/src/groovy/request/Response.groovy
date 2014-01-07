package request

class Response {
	Response (Closure clos){
		this.metaClass['closure']=clos
	}
}
