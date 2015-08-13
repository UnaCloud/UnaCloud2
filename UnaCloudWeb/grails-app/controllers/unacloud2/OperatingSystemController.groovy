package unacloud2

class OperatingSystemController {
	
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of OS services
	 */
	
	OperatingSystemService operatingSystemService
	
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
	 * OS index action
	 * @return list of all OS
	 */
    def index() {
		[oss: OperatingSystem.list(params)];
	}
	
	/**
	 * Creates a new OS. Redirects to index when finished
	 */
	def add() {
		operatingSystemService.addOS(params.name, params.configurer)
		redirect(action:"index")
	}
	
	/**
	 * Deletes the selected OS. Redirects to index when finished
	 */
	
	def delete(){
		def os = OperatingSystem.get(params.id)
		if (!os) {
        redirect(action:"list")
		}
		else{
			operatingSystemService.deleteOS(os)
			redirect(action:"index")
		}
	}
	
	/**
	 * Edit OS form action
	 * @return OS selected by user
	 */
	
	def edit(){
		def o= OperatingSystem.get(params.id)
		
	   	if (!o) {
			redirect(action:"index")
		}
		else{
			[os: o]
		}
	}
	
	/**
	 * Saves OS changes made by the user. Redirects to index when finished 
	 */
	def setValues(){
		def os = OperatingSystem.get(params.id)
		operatingSystemService.setValues(os,params.name,params.configurer)
		redirect(action:"index")
	}
	
}
