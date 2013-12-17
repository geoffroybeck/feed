package spore

import errors.MethodError
import errors.SporeError

class Spore {

	@Mandatory
	def name
	@Mandatory
	def base_url=null
	def authority
	def formats
	def version
	def authentication
	@Mandatory
	List methods=[]// test purposes
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
		if (specErrors.size>0){
			//throw new SporeError()
		}
		/** Saturations of properties 
		 *  with matching parsed JSON entries
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
		 * a parameter Map.
		 * */
		args?."methods".each(){k,v->
			try{
				methods+=k
				def m = createMethod([
					name:k,
					//Inherited from spore if not specified in the parsed Json
					base_url:![null, ""].contains(v['base_url'])?v['base_url']:base_url,
					//Found in the Json [k]
					path:v['path'],
					method:v['method'],
					required_params:v['required_params'],
					optional_params:v['optional_params'],
					expected_status:v['expected_status'],
					required_payload:v['required_payload'],
					description:v['description'],
					authentication:v['authentication'],
					formats:v['formats'],
					documentation:v['documentation'],
					defaults : v['defaults'],
					//Inherited from Spore
					middlewares:middlewares,
					global_authentication:authentication,
					global_formats:formats
				])
				/**Next is the spot where the Method
				 *is dynamically added to the Spore 
				 *If no Method could be created, nothing happens.
				 **/
				m?.class==spore.Method?this.metaClass[k]=m.request:""
			}catch (MethodError me){
				//println me.getMessage()
				//println me.getCause()
			}
		}
	}
	
	/**@param parsedJson : the Json from which the Method should
	 * be created.
	 * @return either a Method either a String describing what prevented 
	 * the method from being created.
	 */
	def createMethod(parsedJson)throws MethodError{
		String message = ""
		def checkResult = methodIntegrityCheck(parsedJson)
		if (checkResult==true){
			return new Method(parsedJson)
		}else{
			checkResult.each(){k,v->
				message+=(k+" : "+v)
			}
			throw new MethodError(message,new Throwable(message))
			return checkResult
		}
	}
	
	/**Checks if the Json data from which the Method is to be created
	 * is sufficient, i.e if it contains the mandatory fields.
	 * @param parsedJson
	 * @return true, if the Json contains sufficient data for required fields, or 
	 * a Map containing error messages registered under the concerned property name
	 */
	def methodIntegrityCheck(parsedJson){
		
		Map methodBuildError=[:]
		List requiredParams = parsedJson['required_params']?:[]
		List optionalParams = parsedJson['optional_params']?:[]
		
		def mandatoryFields=spore.Method.declaredFields.findAll {
			Mandatory in it.declaredAnnotations*.annotationType()
		}*.name
			if (!requiredParams.disjoint(optionalParams)){
				methodBuildError["params"]="params cannot be optional and mandatory at the same time"
		}
		parsedJson.each{k,v->
			if (mandatoryFields.contains(k) && (!v || v.empty || v=='' )){

				methodBuildError[k]="$k is a required field for generated methods, $k couldn't  be generated"

			}
		}
		if (!parsedJson['base_url'] && !parsedJson['api_base_url'] && !base_url){

			methodBuildError['base_url']="Either a base_url or an api_base_url should be specified"

		}
		
		return methodBuildError?.size()==0?true:methodBuildError
	}

	def addDefault(param,value){
		//defaults[params]
	}
	
	def removeDefault(){

	}
	
	def enable(){

	}
	
	def enableIf(Closure clos,predicate,middleware){

	}
}
