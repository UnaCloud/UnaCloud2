package unacloud

import unacloud.enums.NetworkQualityEnum;

class LaboratoryController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of laboratory services
	 */
	LaboratoryService laboratoryService
	
	/**
	 * Representation of group services
	 */
	
	UserGroupService userGroupService

    //-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing user administration actions
	 */
	
	def beforeInterceptor = {
		if(!session.user){
			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		else{
			if(!userGroupService.isAdmin(session.user)){
				flash.message="You must be administrator to see this content"
				redirect(uri:"/error", absolute:true)
				return false
			}
		}
	}
	
	/**
	 * Laboratory index action
	 * @return list of all laboratories
	 */
	def index() {
		[labs: Laboratory.list()]
	}
	
	/**
	 * Create lab form action.
	 * @return list of network configurations
	 */
	def create(){
		[netConfigurations: NetworkQualityEnum.configurations]
	}
	
	
}
