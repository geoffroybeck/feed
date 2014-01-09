class UrlMappings {

	static mappings = {
		"/IAmAWebService/" (controller: "IAmAWebService", action: "vote", parseRequest: true)
		"/IAmAWebService/by/$username/$id" (controller: "IAmAWebService", action: "by", parseRequest: true)
		"/IAmAWebService/descriptionFile/test.json" (controller: "IAmAWebService", action: "descriptionFile", parseRequest: true)
		"/IAmAWebService/descriptionFile/$fileName" (controller: "IAmAWebService", action: "descriptionFile", parseRequest: true)
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
