class UrlMappings {

	static mappings = {
		"/IAmAWebService/" (controller: "IAmAWebService", action: "vote", parseRequest: true)
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
