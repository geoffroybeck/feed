package feed

import spore.Spore

class FeedController {

	static SporeFeeder feed =new SporeFeeder()
	def index() {
		def results=[:]
		
		Spore spore = feed.feed("/home/geoffroy/Documents/workspace/feed/web-app/json/test.json")

		//spore.metaClass.methods*.name.sort().unique()

		def i = 0

	

		spore.enable(spore.Middleware,["elephant":true,"adieu":3,payload:["clef $i":["subclef":'valeur']]])
		spore.enable(spore.Middleware,["allAlongTheWatchTower":true,"thereMustBeSomeWayOutOfHere":0,payload:["clef $i":["subclef":'valeur']]])
		spore.enableIf(spore.Middleware,["blabla":true,"blibli":3,payload:["bonjour":["aurevoir":'demain']]]){
			spore.name!=null
		}
		
		
		spore.middlewares.each{k,v->

			if (k()){
				v.properties.each(){q,r->
				//	println q
				//	println r
				}
			}

		}
		spore?.methods?.each (){
			def test = spore."$it"([content:"tres",test:i,q:"RIGHT",userid:"6666",username:"jean-marc",nextid:"6000",id:"123456",payload:["clef $i":["subclef":'valeur']]])
			i++
			results+=["$it":test]
		}
		def test1 = spore.comments_for_post([test:"test",q:"WRONG",id:"unid"])
		//results += ["comments_for_post":test1]
		//def test99 = spore.vote([test:"test",q:"WRONG",id:"unid",payload:["clef":["subclef":'valeur']]])
		//results += ["comments_for_post":test1]
		//results +=["vote":test99]
		//def test2 = spore.auth_token([authorizationToken:"0uIuio5oka6jejhh",q:"WRONG"])
		//results+=["auth_token":test2]
		//Spore spore3 = feed.feed("/home/geoffroy/workspace/feed/web-app/json/test.json","base_url_de_test")
		//println results
		return [results: results]
	}
}
