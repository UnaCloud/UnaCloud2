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
			"/login"(controller:'user',action:'login')
			"/profile/"(controller:'user',action:'profile')
			"/profile/save"(controller:'user',action:'changeProfile')
			"/profile/change"(controller:'user',action:'changePassword')
			"/profile/change/save"(controller:'user',action:'savePassword')
		}
			
	
		group "/services", {
			
			/** services - my images**/
			group "/image", {
				"/list"(controller:'Image',action:"list")
				"/new"(controller:"Image",action:"newUploadImage")
				"/upload"(controller:"Image",action:"upload")
				"/edit/$id"(controller:'Image',action:"edit")
				"/delete/$id"(controller:'Image',action:"delete")
				"/clear/$id"(controller:'Image',action:"clearFromCache")
				"/public"(controller:'Image',action:'newFromPublic')
				"/public/copy"(controller:'Image',action:'copyPublic')
				"/edit/save"(controller:'Image',action:'saveEdit')
				"/update/$id"(controller:'Image',action:'update')
				"/update/save"(controller:'Image',action:'updateFiles')
			}
						
			/** services - my clusters**/
			group "/cluster", {
				"/list"(controller:'Cluster',action:"list")
				"/new"(controller:"Cluster",action:"newCluster")
				"/save"(controller:"Cluster",action:"save")
				"/delete/$id"(controller:"Cluster",action:"delete")
				"/deploy/$id"(controller:"Cluster",action:"deployOptions")
			}
			
			/** services - deployments **/
			group "/deployment", {
				"/new"(controller:'Deployment',action:"deploy")
				"/list"(controller:'Deployment',action:"list")
				"/stop"(controller:'Deployment',action:"stop")
				"/$id/add"(controller:'Deployment',action:"addInstances")
				"/$id/add/save"(controller:'Deployment',action:"saveInstances")
				"/download/$id"(controller:'Deployment',action:"createCopy")
			}
			
		}	
		
		group "/admin", {
			
			/** admin - users**/
			group "/user", {
				"/list"(controller:'admin',action:"list")
				"/new"(controller:'admin',action:"create")
				"/save"(controller:'admin',action:"save")
				"/delete/$id"(controller:'admin',action:"delete")
				"/edit/$id"(controller:'admin',action:"edit")
				"/edit/save"(controller:'admin',action:"saveEdit")
				"/restrictions/$id"(controller:'admin',action:"config")
				"/restrictions/set"(controller:'admin',action:"setRestrictions")
			}
			
			/** admin - groups**/
			group "/group", {
				"/list"(controller:'userGroup',action:"list")
				"/new"(controller:'userGroup',action:"create")
				"/save"(controller:'userGroup',action:"save")
				"/delete/$id"(controller:'userGroup',action:"delete")
				"/edit/$id"(controller:'userGroup',action:"edit")
				"/edit/save"(controller:'userGroup',action:"saveEdit")
				"/restrictions/$id"(controller:'userGroup',action:"config")
				"/restrictions/set"(controller:'userGroup',action:"setRestrictions")
			}			
			
			/** admin - platforms**/
			group "/platform", {
				"/list"(controller:'platform',action:"list")
				"/new"(controller:'platform',action:"create")
				"/save"(controller:'platform',action:"save")
				"/delete/$id"(controller:'platform',action:"delete")
				"/edit/$id"(controller:'platform',action:"edit")
				"/edit/save"(controller:'platform',action:"saveEdit")
			}			
			
			/** admin - Operating system**/
			group "/os", {
				"/list"(controller:'operatingSystem', action:"list")
				"/new"(controller:'operatingSystem', action:"create")
				"/save"(controller:'operatingSystem', action:"save")
				"/delete/$id"(controller:'operatingSystem', action:"delete")
				"/edit/$id"(controller:'operatingSystem', action:"edit")
				"/edit/save"(controller:'operatingSystem',action:"saveEdit")
			}		
			
			/** admin - Labs management**/
			group "/lab", {
				"/list"(controller:'laboratory', action:"index")
				"/new"(controller:'laboratory', action:"create")
				"/save"(controller:'laboratory', action:"save")
				"/$id"(controller:'laboratory', action:"lab")
				"/$id/new"(controller:'laboratory', action:"createMachine")
				"/$id/save"(controller:'laboratory', action:"saveMachine")
				"/$id/delete/$host"(controller:'laboratory', action:"deleteMachine")
				"/$id/edit/$host"(controller:'laboratory', action:"editMachine")
				"/$id/edit/$host/save"(controller:'laboratory', action:"saveEditMachine")
				"/$id/$process"(controller:'laboratory', action:"updateMachines")
				"/disable/$id"(controller:'laboratory', action:"setStatus")
				"/delete/$id"(controller:'laboratory', action:"delete")
				"/edit/$id"(controller:'laboratory', action:"edit")
				"/edit/save"(controller:'laboratory', action:"saveEdit")
				"/$id/pool/new"(controller:'laboratory', action:"createPool")
				"/$id/pool/save"(controller:'laboratory', action:"savePool")
				"/$id/pool/$pool"(controller:'laboratory', action:"ipList")
				"/$id/pool/delete/$pool"(controller:'laboratory', action:"poolDelete")
				"/$id/pool/$pool/delete/ip/$ip"(controller:'laboratory', action:"ipDelete")
				"/$id/pool/$pool/set/ip/$ip"(controller:'laboratory', action:"ipSet")
			}			
			
			/** admin - Repositories management**/
			group "/repository", {
				"/list"(controller:'repository',action:"list")
				"/new"(controller:'repository',action:"create")
				"/delete/$id"(controller:'repository',action:"delete")
				"/save"(controller:'repository',action:"save")
			}
			
		}
		
		group "/config", {
			/** config - variables**/
			"/variables"(controller:'configuration',action:"listVariables")
			"/variables/set"(controller:'configuration',action:"setVariable")
			
			/** config - agent**/
			"/agent"(controller:'configuration',action:"agentConfig")
			"/agent/version"(controller:'configuration',action:"setAgentVersion")
			"/agent/download"(controller:'configuration',action:"downloadAgent")
		}
					
		/**
		 * API REST services
		 */
		group "/api", {
			"/deployment/"(controller:'DeploymentAPI',action:"create", method:"POST")
		}		
		
	}
}
