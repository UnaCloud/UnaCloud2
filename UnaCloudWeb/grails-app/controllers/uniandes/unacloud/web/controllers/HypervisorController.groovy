package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.services.HypervisorService;
import uniandes.unacloud.web.services.UserGroupService;
import uniandes.unacloud.web.domain.Hypervisor;
import uniandes.unacloud.web.domain.User;

/**
 * This Controller contains actions to manage hypervisor services: deployment crud.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions
 * only administrator users can call this actions.
 * @author CesarF
 *
 */
class HypervisorController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user services
	 */
	
	HypervisorService hypervisorService
	
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
	 * Hypervisor list action
	 * @return list of all hypervisors
	 */
	def list() {
		[hypervisors: Hypervisor.list()];
	}
	
	/**
	 * Creates hypervisor form action
	 */
	def create(){
	}
	
	/**
	 * Saves a new hypervisor based in parameters
	 * redirects to hypervisor list 
	 * @return
	 */
	def save(){
		if(params.name&&params.vers&&params.ext){
			try{
				hypervisorService.create(params.name,params.vers,params.ext,params.files_ext)
				redirect(uri:"/admin/hypervisor/list", absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/hypervisor/new", absolute:true)
			}
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/hypervisor/new", absolute:true)
		}
	}
	
	/**
	 * Edits hypervisor form action.
	 * @return hypervisor selected by user
	 */
	def edit(){
		def hypervisor= Hypervisor.get(params.id)
		if (!hypervisor)
			redirect(uri:"/admin/hypervisor/list", absolute:true)
		else
			[hypervisor:hypervisor]
	}
	
	/**
	 * edits values action. Receives new hypervisor information and sends it to service
	 * Redirects to hypervisor list when finished
	 */
	def saveEdit(){
		if(params.name&&params.version&&params.ext){
			try{
				Hypervisor hypervisor = Hypervisor.get(params.id)
				if(Hypervisor){
					hypervisorService.setValues(hypervisor,params.name,params.version,params.ext,params.files_ext)
					flash.message="Hypervisor values have been modified"
					flash.type="success"
				}	
			}catch(Exception e){
				flash.message=e.message
			}
			redirect(uri:"/admin/hypervisor/list", absolute:true)
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/hypervisor/edit/"+params.id, absolute:true)
		}
	}
	
	/**
	 * Deletes hypervisor action. Redirects to index when finished
	 */	
	def delete(){
		def hypervisor = Hypervisor.get(params.id)
		if (hypervisor) {			
			try{
				hypervisorService.deleteHypervisor(hypervisor)
				flash.message="Your request has been processed"
				flash.type="success"
			}catch(Exception e){
				flash.message=e.message
			}
		}
		redirect(uri:"/admin/hypervisor/list", absolute:true)
	}
}
