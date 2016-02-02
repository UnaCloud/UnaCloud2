class UrlMappings {

	static mappings = {
//        "/$controller/$action?/$id?(.$format)?"{
//            constraints {
//                // apply constraints here
//            }
//        }

//        "/"(view:"/index")
//        "500"(view:'/error')
		"/upload"(controller:"File", action:"upload")
		"/update"(controller:"File", action:"updateFiles")
	}
}
