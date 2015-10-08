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
		"/logout"(controller:'user',action:"logout")
		"/error"(view:'/error')
		/** services - my images**/
		"/services/image/list"(controller:'VirtualMachineImage',action:"list")
		"/services/image/new"(controller:"virtualMachineImage",action:"newUploadImage")
		"/services/image/upload"(controller:"virtualMachineImage",action:"upload")
		"/services/image/edit/$id"(controller:'VirtualMachineImage',action:"edit")
		"/services/image/public"(controller:'VirtualMachineImage',action:'newFromPublic')
		"/services/image/public/copy"(controller:'VirtualMachineImage',action:'copyPublic')
		
		"404"(view:'/error')
		"500"(view:'/error')
		
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
		"/account"(controller:"user", action:"account")
	}
}
