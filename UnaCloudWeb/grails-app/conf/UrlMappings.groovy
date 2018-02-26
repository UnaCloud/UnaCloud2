class UrlMappings {
	
	//static excludes = ["/Image/update"]
	static mappings = {
		
		"/"(controller:"user", action:"index")
		"/home"(controller:"user", action:"home")
		"/login"(view:"/index")
		"/user/login"(controller:'user',action:'login')
		"/logout"(controller:'user',action:"logout")
		"/error"(controller:'error',action:"error")
        "500"(controller:'error',action:"fivehundred")
		"404"(controller:'error',action:"fourhundred")



		/** services - user**/
		"/user/profile/"(controller:'user',action:'profile')
		"/user/profile/save"(controller:'user',action:'changeProfile')
		"/user/profile/change"(controller:'user',action:'changePassword')
		"/user/profile/change/save"(controller:'user',action:'savePassword')
		
		/** services - my images**/
		"/services/image/list"(controller:'Image',action:"list")
		"/services/image/new"(controller:"Image",action:"newUploadImage")
		"/services/image/upload"(controller:"Image",action:"upload")
		"/services/image/edit/$id"(controller:'Image',action:"edit")
		"/services/image/delete/$id"(controller:'Image',action:"delete")
		"/services/image/clear/$id"(controller:'Image',action:"clearFromCache")
		"/services/image/public"(controller:'Image',action:'newFromPublic')
		"/services/image/public/copy"(controller:'Image',action:'copyPublic')
		"/services/image/edit/save"(controller:'Image',action:'saveEdit')
		"/services/image/update/$id"(controller:'Image',action:'update')
		"/services/image/update/save"(controller:'Image',action:'updateFiles')
		
		/** services - my clusters**/
		"/services/cluster/list"(controller:'Cluster',action:"list")
		"/services/cluster/new"(controller:"Cluster",action:"newCluster")
		"/services/cluster/save"(controller:"Cluster",action:"save")
		"/services/cluster/delete/$id"(controller:"Cluster",action:"delete")
		"/services/cluster/deploy/$id"(controller:"Cluster",action:"deployOptions")
		
		/** services - deployments **/
		"/services/deployment/new"(controller:'Deployment',action:"deploy")
		"/services/deployment/list"(controller:'Deployment',action:"list")
		"/services/deployment/stop"(controller:'Deployment',action:"stop")
		"/services/deployment/$id/add"(controller:'Deployment',action:"addInstances")
		"/services/deployment/$id/add/save"(controller:'Deployment',action:"saveInstances")
		"/services/deployment/download/$id"(controller:'Deployment',action:"createCopy")
		
		/** admin - users**/
		"/admin/user/list"(controller:'admin',action:"list")
		"/admin/user/new"(controller:'admin',action:"create")
		"/admin/user/save"(controller:'admin',action:"save")
		"/admin/user/delete/$id"(controller:'admin',action:"delete")
		"/admin/user/edit/$id"(controller:'admin',action:"edit")
		"/admin/user/edit/save"(controller:'admin',action:"saveEdit")
		"/admin/user/restrictions/$id"(controller:'admin',action:"config")
		"/admin/user/restrictions/set"(controller:'admin',action:"setRestrictions")
		
		/** admin - groups**/
		"/admin/group/list"(controller:'userGroup',action:"list")
		"/admin/group/new"(controller:'userGroup',action:"create")
		"/admin/group/save"(controller:'userGroup',action:"save")
		"/admin/group/delete/$id"(controller:'userGroup',action:"delete")
		"/admin/group/edit/$id"(controller:'userGroup',action:"edit")		
		"/admin/group/edit/save"(controller:'userGroup',action:"saveEdit")
		"/admin/group/restrictions/$id"(controller:'userGroup',action:"config")
		"/admin/group/restrictions/set"(controller:'userGroup',action:"setRestrictions")
		
		/** admin - platforms**/
		"/admin/platform/list"(controller:'platform',action:"list")
		"/admin/platform/new"(controller:'platform',action:"create")
		"/admin/platform/save"(controller:'platform',action:"save")
		"/admin/platform/delete/$id"(controller:'platform',action:"delete")
		"/admin/platform/edit/$id"(controller:'platform',action:"edit")
		"/admin/platform/edit/save"(controller:'platform',action:"saveEdit")
		
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

		/** rest api services**/
		 "/rest/deployment"(controller:'DeploymentRest', action:[GET:"list", POST:"deploy", PUT:"stop", DELETE:"delete"])
		"/rest/deployment/$id"(controller:'DeploymentRest', action:[GET:"show"])
		"/rest/deployment/$id/execution/$idExec"(controller:'DeploymentRest', action:[GET:"getExecutionById"])
		"/rest/laboratory"(controller:"LaboratoryRest",action:[PUT:"updateMachines"])
	}
}
