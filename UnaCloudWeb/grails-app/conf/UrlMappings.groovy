class UrlMappings {
	
	static excludes = ["/virtualMachineImage/update"]
	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/"(controller:"user", action:"home")
		"/login"(view:"/index")
		"/test/"(controller:"test", action:"index")
		"/functionalities"(view:"/functionalities")
		"/administration"(view:"/administration")
		"/configuration"(view:"/configuration")
		"/configuration/serverVariables"(view:"/configuration/serverVariables")
		"/configuration/agentVersion"(view:"/configuration/agentVersion")
		"/user/create"(view:"user/create")
		"/hypervisor/create"(view:"hypervisor/create")
		"/operatingSystem/create"(view:"operatingSystem/create")
		"/mainpage"(view:"/mainpage")
		"/home"(controller:"user", action:"home")
		"/adminHome"(controller:"user", action:"adminHome")
		"/userHome"(controller:"user", action:"userHome")
		"/error"(view:'/error')
		"/account"(controller:"user", action:"account")
	}
}
