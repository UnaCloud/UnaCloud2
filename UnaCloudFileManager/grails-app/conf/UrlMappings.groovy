class UrlMappings {

	static mappings = {
		"/"(controller:"File", action:"test")
		"/upload"(controller:"File", action:"upload")
		"/update"(controller:"File", action:"updateFiles")
		"/run"(controller:"File", action:"test")
		"/log/$host/$log"(controller:"File", action:"log")
		
	}
}
