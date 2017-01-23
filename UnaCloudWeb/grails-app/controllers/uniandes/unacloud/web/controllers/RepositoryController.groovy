package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.services.RepositoryService;
import uniandes.unacloud.web.services.UserGroupService;
import uniandes.unacloud.web.domain.Repository;
import uniandes.unacloud.web.domain.User;

/**
 * This Controller contains actions to manage repository services: crud for repositories.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions
 * only administrator users can call this actions.
 * @author CesarF, Carlos
 *
 */
//Agregado por Carlos E. Gomez - diciembre 11 de 2015
class RepositoryController {
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of Repository services
	 */
	
	RepositoryService repositoryService
	
	/**
	 * Representation of group services
	 */ 
	
	UserGroupService userGroupService 
	

	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing any other action
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
	 * OS index action
	 * @return list of all OS
	 */
	def list() {
		[repos: Repository.list()];
	}
	
	/**
	 * renders form to create a new repository
	 */
	def create() {
	}
	
	/**
	 * Creates a new OS. Redirects to repositories list when finished
	 */
	def save() {
		if(params.name&&params.configurer){
			try{
				repositoryService.create(params.name, params.configurer)
				redirect(uri:"/admin/repository/list", absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/repository/new", absolute:true)
			}
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/repository/new", absolute:true)
		}
	}
	
	/**
	 * Deletes the selected repositorie. Redirects to index when finished
	 */
	
	def delete(){
		def repo = Repository.get(params.id)
		if (repo) {
			try{
				repositoryService.delete(repo)
				flash.message="Your request has been processed"
				flash.type="success"
			}catch(Exception e){
				flash.message=e.message
			}
		}
		redirect(uri:"/admin/repository/list", absolute:true)
	}

}
