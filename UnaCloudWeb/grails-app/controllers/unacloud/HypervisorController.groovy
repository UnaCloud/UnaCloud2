package unacloud

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
			if(!userGroupService.isAdmin(session.user)){
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
	 * Create hypervisor form action
	 */
	def create(){
	}
	
	/**
	 * Save a new hypervisor based in parameters
	 * redirects to hypervisor list 
	 * @return
	 */
	def save(){
		if(params.name&&params.version){
			try{
				hypervisorService.create(params.name,params.version)
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
	
}
