package unacloud

import unacloud.enums.UserStateEnum;

class DeploymentController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of cluster service
	 */
	DeploymentService deploymentService

	/**
	 * Makes session verifications before executing any action
	 */
	
	def beforeInterceptor = {
		if(!session.user){
			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		session.user.refresh()
		if(!session.user.status.equals(UserStateEnum.AVAILABLE)){
			flash.message="Your user does not have permissions to do this action"
			redirect(uri:"/", absolute:true)
			return false
		}
	}
	
	/**
	 * Deploy options action that brings the form with deploying options for each
	 * image
	 * @return limits shown in the information of form and cluster to be deployed
	 */
    def configDeployment() { 
		def cluster=Cluster.get(params.cluster);
		int limit
		int limitHA
		def user = User.get(session.user.id)
		def machines = userRestrictionProcessorService.getAvoidedMachines(user)
		limitHA = machines.findAll{it.highAvailability==true}.size()
		limit = machines.size() - limitHA;
		int maxDeploys = clusterService.calculateMaxDeployments(user, HardwareProfile.findByName("small"))
		[cluster: cluster,limit: limit, limitHA: limitHA, hardwareProfiles: HardwareProfile.list(), max:maxDeploys]		
	}
}
