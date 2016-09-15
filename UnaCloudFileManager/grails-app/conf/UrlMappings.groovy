class UrlMappings {

	static mappings = {
		"/upload"(controller:"File", action:"upload")
		"/update"(controller:"File", action:"updateFiles")
		"/run"(controller:"File", action:"test")
	}
}
