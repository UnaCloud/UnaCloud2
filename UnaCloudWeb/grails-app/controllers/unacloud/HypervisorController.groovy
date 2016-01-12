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
	
	/**
	 * Edit hypervisor form action.
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
	 * edit values action. Receives new hypervisor information and sends it to service
	 * Redirects to hypervisor list when finished
	 */
	def saveEdit(){
		if(params.name&&params.version){
			try{
				Hypervisor hypervisor = Hypervisor.get(params.id)
				if(Hypervisor){
					hypervisorService.setValues(hypervisor,params.name,params.version)
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
	 * Delete hypervisor action. Redirects to index when finished
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
