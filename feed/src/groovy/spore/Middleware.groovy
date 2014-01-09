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
		//ici c'est encore faux mec
		//bon alors t'es pas obligé de te fonder sur une convention de nommage
		//
		if (this?.metaClass.methods*.name.contains('processRequest')){
			
			def ret =this?.processRequest(params)
			return ret
		}else if (this?.metaClass.methods*.name.contains('processResponse')){
			
			 this?.processResponse(params)
			
	
		}else return null
	}
}
