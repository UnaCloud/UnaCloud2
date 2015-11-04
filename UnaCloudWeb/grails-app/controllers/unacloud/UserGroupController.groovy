package unacloud

class UserGroupController {

	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user services
	 */
	
	UserService userService
	
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
	 * Index action
	 * @return list with all groups
	 */
	
	def list() {
		[groups: UserGroup.list()]
	}
	
	/**
	 * Create group form action
	 * @return list with all user for group creation
	 */
	def create(){
		[users: User.list()]
	}
	
	/**
	 * Save group action. Redirects to group list when finished.
	 */
	def add(){
		if(params.name){
			try{
				groupService.addGroup(params.name,params.users,session.user)
				redirect(uri:"/admin/group/list", absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/group/new", absolute:true)
			}
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/group/new", absolute:true)
		}
	}
	
}
