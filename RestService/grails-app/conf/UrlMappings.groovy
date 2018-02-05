class UrlMappings {

	static mappings = {
        "/$controller/$id?"{
            action = [GET:"show", POST:"save", PUT:"update", DELETE:"delete"]
        }
        "/$controller/"{
            action=[GET:"index"]
        }

        "/"(view:"/index")
        "500"(view:'/error')
	}
}