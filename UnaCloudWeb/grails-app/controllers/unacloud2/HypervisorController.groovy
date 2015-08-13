package unacloud2

class HypervisorController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of hypervisor services
	 */
	
	HypervisorService hypervisorService
	
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
		else if(!(session.user.userType.equals("Administrator"))){
			flash.message="You must be administrator to see this content"
			redirect(uri:"/error", absolute:true)
			return false
		}
	}
	
	/**
	 * Hypervisor index action
	 * @return list of all hypervisors
	 */
	def index() {
		[hypervisors: Hypervisor.list(params)];
	}
	
	/**
	 * Add hypervisor action. Redirects to index when finished
	 */
	
	def add() {
		hypervisorService.addHypervisor(params.name, params.hyperVersion)
		redirect(action:"index")
	}
	
	/**
	 * Delete hypervisor action. Redirects to index when finished 
	 */
	
	def delete(){
		def hypervisor = Hypervisor.get(params.id)
		if (!hypervisor) {
        redirect(action:"list")
		}
		else{
			hypervisorService.deleteHypervisor(hypervisor)
			redirect(action:"index")
		}
	}
	
	/**
	 * Edit hypervisor form action.
	 * @return hypervisor selected by user
	 */
	def edit(){
		def h= Hypervisor.get(params.id)
		
	   	if (!h) {
			redirect(action:"index")
		}
		else{
			[hypervisor: h]
		}
	}
	
	/**
	 * Saves changes of edited hypervisor. Redirects to index when finished
	*/
	def setValues(){
		def hypervisor = Hypervisor.get(params.id)
		hypervisorService.setValues(hypervisor, params.name)
		redirect(action:"index")
	}
	
}
