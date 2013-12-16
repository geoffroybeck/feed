package feed

import spore.Spore

class FeedController {

	static SporeFeeder feed =new SporeFeeder()
	def index() {
		def results=[:]
		
		Spore spore = feed.feed("/home/geoffroy/Documents/workspace/feed/web-app/json/test.json")
		
	//	println "!"+spore.metaClass.methods*.name.sort().unique()
		
		def i = 0
		
		spore?.methods?.each (){
			def test = spore."$it"([test:i,q:"RIGHT"])
			i++
			results+=["$it":test]
		}
		
		def test1 = spore.comments_for_post([test:"test",q:"WRONG",id:"unid"])
		results += ["comments_for_post":test1]
		def test99 = spore.vote([test:"test",q:"WRONG",id:"unid"])
		results += ["comments_for_post":test1]
		results +=["vote":test99]
		/*
		 spore.metaClass.foo="foo"
		 println "1"+spore.bark('bonjour')
		 spore.metaClass.bark= fancyUntypedClosure
		 println "2"+spore.bark('bonjour')
		*/
		
		def test2 = spore.auth_token([authorizationToken:"0uIuio5oka6jejhh",q:"WRONG"])
		results+=["auth_token":test2]
		//Spore spore3 = feed.feed("/home/geoffroy/workspace/feed/web-app/json/test.json","base_url_de_test")
		return [results: results]
		}
}
