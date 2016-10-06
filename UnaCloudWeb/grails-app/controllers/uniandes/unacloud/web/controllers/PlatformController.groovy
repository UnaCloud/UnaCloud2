package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.services.ImageService
import uniandes.unacloud.web.services.LaboratoryService
import uniandes.unacloud.web.services.PlatformService;
import uniandes.unacloud.web.services.UserGroupService;
import uniandes.unacloud.web.domain.Platform;
import uniandes.unacloud.web.domain.User;

/**
 * This Controller contains actions to manage platform services: deployment crud.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions
 * only administrator users can call this actions.
 * @author CesarF
 *
 */
class PlatformController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user services
	 */
	
	PlatformService platformService
	
	
	/**
	 * Representation of image services
	 */
	
	ImageService imageService
	
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
			def user = User.get(session.user.id)
			session.user.refresh(user)
			if(!userGroupService.isAdmin(user)){
				flash.message="You must be administrator to see this content"
				redirect(uri:"/error", absolute:true)
				return false
			}
		}
	}
	
	/**
	 * Platform list action
	 * @return list of all platform
	 */
	def list() {
		[platforms: Platform.list()];
	}
	
	/**
	 * Creates platform form action
	 */
	def create(){
	}
	
	/**
	 * Saves a new Platform based in parameters
	 * redirects to platform list 
	 * @return
	 */
	def save(){
		if(params.name&&params.vers&&params.ext&&params.cls){
			try{
				platformService.create(params.name,params.vers,params.ext,params.files_ext, params.cls)
				redirect(uri:"/admin/platform/list", absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/platform/new", absolute:true)
			}
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/platform/new", absolute:true)
		}
	}
	
	/**
	 * Edits platform form action.
	 * @return platform selected by user
	 */
	def edit(){
		def platform= Platform.get(params.id)
		if (!platform)
			redirect(uri:"/admin/platform/list", absolute:true)
		else
			[platform:platform]
	}
	
	/**
	 * edits values action. Receives new platform information and sends it to service
	 * Redirects to platform list when finished
	 */
	def saveEdit(){
		if(params.name&&params.vers&&params.ext&&params.cls){
			try{
				Platform platform = Platform.get(params.id)
				if(Platform){
					platformService.setValues(platform,params.name,params.vers,params.ext,params.files_ext,params.cls)
					flash.message="Platform values have been modified"
					flash.type="success"
				}	
			}catch(Exception e){
				flash.message=e.message
			}
			redirect(uri:"/admin/platform/list", absolute:true)
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/platform/edit/"+params.id, absolute:true)
		}
	}
	
	/**
	 * Deletes platform action. Redirects to index when finished
	 */	
	def delete(){
		def platform = Platform.get(params.id)
		if (platform) {			
			try{
				def listImages = imageService.getListMachinesByPlatform(platform)
				if(!listImages||listImages.size()==0){
					platformService.deletePlatform(platform)
					flash.message="Your request has been processed"
					flash.type="success"
				}
				else{
					flash.message="Platform is being used in some images, please delete images first"
					flash.type="success"
				}
			}catch(Exception e){
				flash.message=e.message
			}
		}
		redirect(uri:"/admin/platform/list", absolute:true)
	}
}
