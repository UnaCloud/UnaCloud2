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
	
	/**
	 * Save created lab action. Redirects to list when finished 
	 */
	def save(){
		if(params.name&&NetworkQualityEnum.getNetworkQuality(params.net)!=null
			&&params.netGateway&&params.netMask&&params.ipInit&&params.ipEnd){
			println params.ipInit
			println params.ipEnd
			try{
				laboratoryService.createLab(params.name, (params.isHigh!=null),NetworkQualityEnum.getNetworkQuality(params.net), (params.isPrivate!=null),params.netGateway, params.netMask,params.ipInit,params.ipEnd);
				redirect(uri:"/admin/lab/list", absolute:true)
			}catch(Exception e){
				flash.message="Error: "+e.message
				redirect(uri:"/admin/lab/new", absolute:true)
			}
			
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/lab/new", absolute:true)
		}
	}
	
}
