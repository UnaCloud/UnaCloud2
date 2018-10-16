class UrlMappings {
	
	//static excludes = ["/Image/update"]
	static mappings = {
		
		"/"(controller:"user", action:"index")
		"/home"(controller:"user", action:"home")
		"/login"(view:"/index")		
		"/logout"(controller:'user', action:"logout")
		"/error"(controller:'error', action:"error")
		"404"(controller:'error', action:"fourhundred")
		"500"(controller:'error', action:"fivehundred")
				
		/** user**/
		group "/user", {
			"/login"(controller:'user', action:'login')
			"/profile/"(controller:'user', action:'profile')
			"/profile/save"(controller:'user', action:'changeProfile')
			"/profile/change"(controller:'user', action:'changePassword')
			"/profile/change/save"(controller:'user', action:'savePassword')
		}
	
		group "/services", {
						
			/** services - my images**/			
			"/image/list"(controller:'Image', action:"list")
			"/image/new"(controller:"Image", action:"newUploadImage")
			"/image/upload"(controller:"Image", action:"upload")
			"/image/edit/$id"(controller:'Image', action:"edit")
			"/image/delete/$id"(controller:'Image', action:"delete")
			"/image/clear/$id"(controller:'Image', action:"clearFromCache")
			"/image/public"(controller:'Image', action:'newFromPublic')
			"/image/public/copy"(controller:'Image', action:'copyPublic')
			"/image/edit/save"(controller:'Image', action:'saveEdit')
			"/image/update/$id"(controller:'Image', action:'update')
			"/image/update/save"(controller:'Image', action:'updateFiles')
					
			/** services - my clusters**/			
			"/cluster/list"(controller:'Cluster', action:"list")
			"/cluster/new"(controller:"Cluster", action:"newCluster")
			"/cluster/save"(controller:"Cluster", action:"save")
			"/cluster/delete/$id"(controller:"Cluster", action:"delete")
			"/cluster/deploy/$id"(controller:"Cluster", action:"deployOptions")
						
			/** services - deployments **/
			"/deployment/new"(controller:'Deployment', action:"deploy")
			"/deployment/list"(controller:'Deployment', action:"list")
			"/deployment/stop"(controller:'Deployment', action:"stop")
			"/deployment/$id/add"(controller:'Deployment', action:"addInstances")
			"/deployment/$id/add/save"(controller:'Deployment', action:"saveInstances")
			"/deployment/download/$id"(controller:'Deployment', action:"createCopy")
					
		}	
		
		group "/admin", {
			
			/** admin - users**/			
			"/user/list"(controller:'admin', action:"list")
			"/user/new"(controller:'admin', action:"create")
			"/user/save"(controller:'admin', action:"save")
			"/user/delete/$id"(controller:'admin', action:"delete")
			"/user/edit/$id"(controller:'admin', action:"edit")
			"/user/edit/save"(controller:'admin', action:"saveEdit")
			"/user/restrictions/$id"(controller:'admin', action:"config")
			"/user/restrictions/set"(controller:'admin', action:"setRestrictions")
						
			/** admin - groups**/		
			"/group/list"(controller:'userGroup', action:"list")
			"/group/new"(controller:'userGroup', action:"create")
			"/group/save"(controller:'userGroup', action:"save")
			"/group/delete/$id"(controller:'userGroup', action:"delete")
			"/group/edit/$id"(controller:'userGroup', action:"edit")
			"/group/edit/save"(controller:'userGroup', action:"saveEdit")
			"/group/restrictions/$id"(controller:'userGroup', action:"config")
			"/group/restrictions/set"(controller:'userGroup', action:"setRestrictions")
		
			/** admin - platforms**/		
			"/platform/list"(controller:'platform', action:"list")
			"/platform/new"(controller:'platform', action:"create")
			"/platform/save"(controller:'platform', action:"save")
			"/platform/delete/$id"(controller:'platform', action:"delete")
			"/platform/edit/$id"(controller:'platform', action:"edit")
			"/platform/edit/save"(controller:'platform', action:"saveEdit")
			
			/** admin - Operating system**/		
			"/os/list"(controller:'operatingSystem', action:"list")
			"/os/new"(controller:'operatingSystem', action:"create")
			"/os/save"(controller:'operatingSystem', action:"save")
			"/os/delete/$id"(controller:'operatingSystem', action:"delete")
			"/os/edit/$id"(controller:'operatingSystem', action:"edit")
			"/os/edit/save"(controller:'operatingSystem', action:"saveEdit")
								
			/** admin - Labs management**/		
			"/lab/list"(controller:'laboratory', action:"index")
			"/lab/new"(controller:'laboratory', action:"create")
			"/lab/save"(controller:'laboratory', action:"save")
			"/lab/$id"(controller:'laboratory', action:"lab")
			"/lab/$id/delete"(controller:'laboratory', action:"delete")
			"/lab/$id/disable"(controller:'laboratory', action:"setStatus")
			"/lab/$id/edit"(controller:'laboratory', action:"edit")
			"/lab/$id/save"(controller:'laboratory', action:"saveEdit")
			
			/** admin - Physical machines**/			
			"/lab/$id/machine/new"(controller:'machine', action:"create")
			"/lab/$id/machine/save"(controller:'machine', action:"save")
			"/lab/$id/machine/$host"(controller:'machine', action:"index")
			"/lab/$id/machine/$host/delete"(controller:'machine', action:"delete")
			"/lab/$id/machine/$host/edit"(controller:'machine', action:"edit")
			"/lab/$id/machine/$host/save"(controller:'machine', action:"saveEdit")				
			"/lab/$id/machine/task/$process"(controller:'machine', action:"requestTask")
			
			/** admin - IP**/	
			"/lab/$id/pool/new"(controller:'ip', action:"createPool")
			"/lab/$id/pool/save"(controller:'ip', action:"savePool")
			"/lab/$id/pool/$pool"(controller:'ip', action:"list")
			"/lab/$id/pool/$pool/delete/"(controller:'ip', action:"poolDelete")
			"/lab/$id/pool/$pool/ip/$ip/delete"(controller:'ip', action:"delete")
			"/lab/$id/pool/$pool/ip/$ip/set"(controller:'ip', action:"update")
						
			/** admin - Repositories management**/
			"/repository/list"(controller:'repository', action:"list")
			"/repository/new"(controller:'repository', action:"create")
			"/repository/delete/$id"(controller:'repository', action:"delete")
			"/repository/save"(controller:'repository', action:"save")
						
		}
		
		group "/config", {
			
			/** config - variables**/
			"/variables"(controller:'configuration', action:"listVariables")
			"/variables/set"(controller:'configuration', action:"setVariable")
			
			/** config - agent**/
			"/agent"(controller:'configuration', action:"agentConfig")
			"/agent/version"(controller:'configuration', action:"setAgentVersion")
			"/agent/download"(controller:'configuration', action:"downloadAgent")
		}

		/**
		 * API REST services
		 */
		group "/rest", {
			"/deployments"(controller:'DeploymentRest', action:[GET:"list", POST:"deploy", PUT:"stop", DELETE:"delete"])
			"/deployments/$id"(controller:'DeploymentRest', action:[GET:"show"])
			"/deployments/$id/executionIps/"(controller:'DeploymentRest', action:[GET:"getIpsOfDeployment"])
			"/deployments/$id/executions/$idExec"(controller:'DeploymentRest', action:[GET:"getExecutionById"])
			"/deployments/$id/executions/$idExec/histories"(controller:'DeploymentRest', action:[GET:"getExecutionHistory"])
			"/deployments/$id/deployedImages/$imageId"(controller:'DeploymentRest', action:[GET:"getExecutionsByDeployedImagetId"])
			"/reports/executionsData/$name" (controller:"ExecutionRest", action:[GET:"findByName"])
			"/laboratories"(controller:"MachineRest",action:[PUT:"updateMachines"])
			"/laboratories/$id/machines"(controller:"MachineRest",action:[GET:"getLaboratoryMachines"])
		}
		
	}
}
