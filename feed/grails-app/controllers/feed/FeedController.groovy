package feed

import spore.Spore
import request.Response

class FeedController {

	static SporeFeeder feed =new SporeFeeder()
	
	def index() {
		def results=[:]
		//"/home/geoffroy/Documents/workspace/feed/web-app/json/test.json"
		//Spore spore = feed.feed("/home/geoffroy/Documents/workspace/feed/web-app/json/test.json")
		Spore spore = feed.feed("http://localhost:8080/feed/IAmAWebService/descriptionFile/test.json")
		def i = 0
		
		spore.enable(
				spore.Middleware,
				[
					"elephant":true,
					processRequest:{args->
						 args['spore.headers'] = ["Authorization":64536546]
						 return null
						 },
					"adieu":3,
					payload:["blo":["blo":'blo']]
				]
				)
		spore.enable(
			spore.Middleware,
			[
				"patate":true,
				processRequest:{args->
					 args['spore.headers'] = ["Authorization":64536546]
					 return {
						 "blabla"
					 }
					 },
				"adieu":3,
				payload:["blo":["blo":'blo']]
			]
			)
		spore.enable(
			spore.Middleware,
			[
				param1:true,
				processRequest:{args->
						 args['spore.headers'] = ["Authorization":64536546]
						 Response r = new Response(
							 ["ouais":{
							 "status : 500"
						 }]
							 )
						 return r
						 },
				param2:3,
				payload:[
					"blo":["blo":'blo']
					]
			]
			)
		spore.enable(
			spore.Middleware,
			[
				"patate":true,
				processRequest:{args->
					 args['spore.headers'] = ["Authorization":64536546]
					 return {
						 "blabla"
					 }
					 },
				"adieu":3,
				payload:["blo":["blo":'blo']]
			]
		)
		println "OUAIS"
		/*def methods = spore.metaClass.methods*.name.sort().unique()
		println "mmmmmmmmmmmmmmmmmmmmm" +methods
		def metaMethods = spore.metaClass.metaMethods*.name.sort().unique()
		println "meta"+metaMethods*/
		/*
		 spore.enable(spore.Middleware,["allAlongTheWatchTower":true,"callback":{args->println "WRONG : $args"},"thereMustBeSomeWayOutOfHere":0,payload:["clef $i":["subclef":'valeur']]])
		 spore.enableIf(spore.Middleware,["blabla":true,"blibli":3,payload:["bonjour":["aurevoir":'demain']]]){
		 spore.name!=null
		 }
		 */
		
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
		results += ["comments_for_post":test1]
		
		//def test99 = spore.vote([test:"test",q:"WRONG",id:"unid",payload:["clef":["subclef":'valeur']]])
		//results += ["comments_for_post":test1]
		//results +=["vote":test99]
		//def test2 = spore.auth_token([authorizationToken:"0uIuio5oka6jejhh",q:"WRONG"])
		//results+=["auth_token":test2]
		return [results: results]
	}
}
