class UrlMappings {
	
	static excludes = ["/virtualMachineImage/update"]
	static mappings = {
		/**"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}**/
		"/"(controller:"user", action:"home")
		"/login"(view:"/index")
		"/user/login"(controller:'user',action:'login')
		"/logout"(controller:'user',action:"logout")
		"/error"(view:'/error')
		"404"(view:'/error')
		"500"(view:'/error')
		
		/** services - my images**/
		"/services/image/list"(controller:'VirtualMachineImage',action:"list")
		"/services/image/new"(controller:"VirtualMachineImage",action:"newUploadImage")
		"/services/image/upload"(controller:"VirtualMachineImage",action:"upload")
		"/services/image/edit/$id"(controller:'VirtualMachineImage',action:"edit")
		"/services/image/delete/$id"(controller:'VirtualMachineImage',action:"delete")
		"/services/image/clear/$id"(controller:'VirtualMachineImage',action:"clearFromCache")
		"/services/image/public"(controller:'VirtualMachineImage',action:'newFromPublic')
		"/services/image/public/copy"(controller:'VirtualMachineImage',action:'copyPublic')
		"/services/image/edit/save"(controller:'VirtualMachineImage',action:'saveEdit')
		"/services/image/external/$id"(controller:'VirtualMachineImage',action:'external')
		"/services/image/update/$id"(controller:'VirtualMachineImage',action:'update')
		"/services/image/update/save"(controller:'VirtualMachineImage',action:'updateFiles')
		
		/** services - my clusters**/
		"/services/cluster/list"(controller:'Cluster',action:"list")
		"/services/cluster/new"(controller:"Cluster",action:"newCluster")
		"/services/cluster/save"(controller:"Cluster",action:"save")
		"/services/cluster/delete/$id"(controller:"Cluster",action:"delete")
		"/services/cluster/deploy/$id"(controller:"Cluster",action:"deployOptions")
		"/services/cluster/external/$id"(controller:"Cluster",action:"externalDeployOptions")		
		
		/** admin - users**/
		"/admin/user/list"(controller:'user',action:"list")
		"/admin/user/new"(controller:'user',action:"create")
		"/admin/user/save"(controller:'user',action:"save")
		"/admin/user/delete/$id"(controller:'user',action:"delete")
		"/admin/user/edit/$id"(controller:'user',action:"edit")
		"/admin/user/edit/save"(controller:'user',action:"saveEdit")
		
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
