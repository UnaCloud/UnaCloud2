package unacloud

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
	 * render form to create a new repository
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
