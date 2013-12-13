package spore

import errors.SporeError

class Spore {

	@Mandatory def name
	@Mandatory def base_url=null
	def authority
	def formats
	def version
	def authentication
	@Mandatory List methods=[]// test purposes
	def meta
	def middlewares
	def user_agent

	/**Explicit constructor*/
	Spore(args){

		def specErrors=[:]
		def methodErrors=[:]

		/**Get all properties declared as mandatory*/
		def mandatoryFields=this.properties.findAll { prop ->
			this.getClass().declaredFields.find {
				it.name == prop.key && Mandatory in it.declaredAnnotations*.annotationType()
			}
		}.keySet()
		mandatoryFields.each(){
			if (!args."$it") specErrors[it]="quoi?"
		}
		if (!specErrors.empty){
			println "?"+specErrors+"?"
			//throw new SporeError()
		}
		/** Saturations of properties 
		 * with matching parsed JSON keys' values.
		 */
		args?.each(){k,v->
			if (this.properties.find({it.key==k && !['methods'].contains(k)})){
				this."$k"=v
			}
		}
		/**For each entry in the set of elements
		 * found under "methods" in the parsed
		 * JSON, a Method instance is generated,
		 * and its request property, which is a 
		 * closure, is added to the Spore's methods
		 * under the matching name, with a one parameter 
		 * signature that is gonna need to be fulfilled with
		 * parameter Map.
		 * */
		args?."methods".each(){k,v->
			methods+=k
			Method m = new Method(
					name:k,
					base_url:args['base_url'],
					middlewares:middlewares,
					global_authentication:authentication,
					global_formats:formats
					//defaults:defaults,
					)
			this.metaClass[k]=m.request
		}
	}
	def doStuff(name){
		"$name"()
	}
	def bark(string){
		"oui : $string"
	}
}
