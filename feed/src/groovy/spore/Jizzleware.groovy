package spore
import java.lang.reflect.Method

class Jizzleware extends Middleware{
	
	public Jizzleware(Object arg0) {
		super(arg0);
	}
	
	public java.lang.reflect.Method conditionJizzer(){
		def condition
		Method[] methods = this.getClass().getMethods();
		methods.each{
			if (it.getName()=="condition"){
				condition = it
			}
		}
		return condition
	}
	public static boolean condition(args){
		
	}
}
