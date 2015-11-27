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
	
}
