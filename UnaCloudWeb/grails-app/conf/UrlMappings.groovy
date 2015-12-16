class UrlMappings {
	
	static excludes = ["/virtualMachineImage/update"]
	static mappings = {
		
		"/"(controller:"user", action:"index")
		"/home"(controller:"user", action:"home")
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
		
		/** services - deployments **/
		"/services/deployment/new"(controller:'Deployment',action:"deploy")
		"/services/deployment/list"(controller:'Deployment',action:"list")
		"/services/deployment/stop"(controller:'Deployment',action:"stop")
		"/services/deployment/$id/add"(controller:'Deployment',action:"addInstances")
		"/services/deployment/$id/add/save"(controller:'Deployment',action:"saveInstances")
		"/services/deployment/download/$id"(controller:'Deployment',action:"createCopy")
		
		/** admin - users**/
		"/admin/user/list"(controller:'user',action:"list")
		"/admin/user/new"(controller:'user',action:"create")
		"/admin/user/save"(controller:'user',action:"save")
		"/admin/user/delete/$id"(controller:'user',action:"delete")
		"/admin/user/edit/$id"(controller:'user',action:"edit")
		"/admin/user/edit/save"(controller:'user',action:"saveEdit")
		"/admin/user/restrictions/$id"(controller:'user',action:"config")
		"/admin/user/restrictions/set"(controller:'user',action:"setRestrictions")
		
		/** admin - groups**/
		"/admin/group/list"(controller:'userGroup',action:"list")
		"/admin/group/new"(controller:'userGroup',action:"create")
		"/admin/group/save"(controller:'userGroup',action:"save")
		"/admin/group/delete/$id"(controller:'userGroup',action:"delete")
		"/admin/group/edit/$id"(controller:'userGroup',action:"edit")		
		"/admin/group/edit/save"(controller:'userGroup',action:"saveEdit")
		"/admin/group/restrictions/$id"(controller:'userGroup',action:"config")
		"/admin/group/restrictions/set"(controller:'userGroup',action:"setRestrictions")
		
		/** admin - hypervisors**/
		"/admin/hypervisor/list"(controller:'hypervisor',action:"list")
		"/admin/hypervisor/new"(controller:'hypervisor',action:"create")
		"/admin/hypervisor/save"(controller:'hypervisor',action:"save")
		"/admin/hypervisor/delete/$id"(controller:'hypervisor',action:"delete")
		"/admin/hypervisor/edit/$id"(controller:'hypervisor',action:"edit")
		"/admin/hypervisor/edit/save"(controller:'hypervisor',action:"saveEdit")
		
		/** admin - Operating system**/
		"/admin/os/list"(controller:'operatingSystem',action:"list")
		"/admin/os/new"(controller:'operatingSystem',action:"create")
		"/admin/os/save"(controller:'operatingSystem',action:"save")
		"/admin/os/delete/$id"(controller:'operatingSystem',action:"delete")
		"/admin/os/edit/$id"(controller:'operatingSystem',action:"edit")
		"/admin/os/edit/save"(controller:'operatingSystem',action:"saveEdit")
		
		/** admin - Labs management**/
		"/admin/lab/list"(controller:'laboratory',action:"index")
		"/admin/lab/new"(controller:'laboratory',action:"create")
		"/admin/lab/save"(controller:'laboratory',action:"save")
		"/admin/lab/$id"(controller:'laboratory',action:"lab")
		"/admin/lab/$id/new"(controller:'laboratory',action:"createMachine")
		"/admin/lab/$id/save"(controller:'laboratory',action:"saveMachine")
		"/admin/lab/$id/delete/$host"(controller:'laboratory',action:"deleteMachine")
		"/admin/lab/$id/edit/$host"(controller:'laboratory',action:"editMachine")
		"/admin/lab/$id/edit/$host/save"(controller:'laboratory',action:"saveEditMachine")
		"/admin/lab/$id/$process"(controller:'laboratory',action:"updateMachines")
		"/admin/lab/disable/$id"(controller:'laboratory',action:"setStatus")
		"/admin/lab/delete/$id"(controller:'laboratory',action:"delete")
		"/admin/lab/edit/$id"(controller:'laboratory',action:"edit")
		"/admin/lab/edit/save"(controller:'laboratory',action:"saveEdit")
		"/admin/lab/$id/pool/new"(controller:'laboratory',action:"createPool")
		"/admin/lab/$id/pool/save"(controller:'laboratory',action:"savePool")
		"/admin/lab/$id/pool/$pool"(controller:'laboratory',action:"ipList")
		"/admin/lab/$id/pool/delete/$pool"(controller:'laboratory',action:"poolDelete")
		"/admin/lab/$id/pool/$pool/delete/ip/$ip"(controller:'laboratory',action:"ipDelete")
		"/admin/lab/$id/pool/$pool/set/ip/$ip"(controller:'laboratory',action:"ipSet")
		
		
		/** admin - Repositories management**/
		// Agregado por Carlos E. Gomez - diciembre 11 de 2015
		"/admin/repository/list"(controller:'repository',action:"list")
		"/admin/repository/new"(controller:'repository',action:"create")
		"/admin/repository/delete/$id"(controller:'repository',action:"delete")
		"/admin/repository/save"(controller:'repository',action:"save")
		
		/** config - variables**/		
		"/config/variables"(controller:'configuration',action:"listVariables")
		"/config/variables/set"(controller:'configuration',action:"setVariable")
		
		/** config - agent**/
		"/config/agent"(controller:'configuration',action:"agentConfig")
		"/config/agent/version"(controller:'configuration',action:"setAgentVersion")
		"/config/agent/download"(controller:'configuration',action:"downloadAgent")
	}
}
