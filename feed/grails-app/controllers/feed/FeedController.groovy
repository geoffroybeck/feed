package feed

import spore.Spore

class FeedController {

	static SporeFeeder feed =new SporeFeeder()
	def index() {
		
		Spore spore = feed.feed("/home/geoffroy/workspace/feed/web-app/json/test.json")
		
		println spore.metaClass.methods*.name.sort().unique()
		
		spore.properties.each (){k,v->
			println "propName"+k
			println "propVal"+v
		}
		def i = 0
		spore?.methods?.each (){
			/*println "invokeMethod"
			spore.invokeMethod(it, [])
			println "doStuff"
			spore.doStuff(it)
			println "GString"*/
			println "$it"
			spore."$it"(i)
			i++
		}
		spore.comments_for_post([test:"test"])
		/*def spore = new Spore()
		 spore.metaClass.foo="foo"
		 println "1"+spore.bark('bonjour')
		 spore.metaClass.bark= fancyUntypedClosure
		 println "2"+spore.bark('bonjour')
		 spore.properties.each(){k,v->
		 if (k=="base_url"){
		 }
		 }*/
	}

}
