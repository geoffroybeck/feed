package spore

class Middleware {
	
	//Explicit constructor
	def Middleware(args){
		args.each{k,v->
			this.metaClass."$k"=v
		}
	}
	def call={params->
		
		params.each {propName,propVal->
			
			//this.metaClass."$propName"=propVal
			
		}
		if (this?.metaClass.methods*.name.contains('processRequest')){
			
			def ret =this?.processRequest(params)
			return ret
		}else if (this?.metaClass.methods*.name.contains('processResponse')){
			
			 this?.processResponse(params)
			
	
		}else return null
	}
}
