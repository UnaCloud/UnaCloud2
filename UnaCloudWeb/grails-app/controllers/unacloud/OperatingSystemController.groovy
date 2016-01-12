package unacloud

class OperatingSystemController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of OS services
	 */
	
	OperatingSystemService operatingSystemService
	
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
		[oss: OperatingSystem.list()];
	}
	
	/**
	 * render form to create a new OS
	 */
	def create() {
	}
	
	/**
	 * Creates a new OS. Redirects to os list when finished
	 */
	def save() {
		if(params.name&&params.configurer){
			try{
				operatingSystemService.create(params.name, params.configurer)
				redirect(uri:"/admin/os/list", absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/os/new", absolute:true)
			}
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/os/new", absolute:true)
		}
	}
	
	/**
	 * Deletes the selected OS. Redirects to index when finished
	 */
	
	def delete(){
		def os = OperatingSystem.get(params.id)
		if (os) {
			try{
				operatingSystemService.delete(os)
				flash.message="Your request has been processed"
				flash.type="success"
			}catch(Exception e){
				flash.message=e.message
			}
		}
		redirect(uri:"/admin/os/list", absolute:true)
	}
	
	/**
	 * Edit OS form action
	 * @return OS selected by user
	 */
	
	def edit(){
		def os= OperatingSystem.get(params.id)
		if (!os)
			redirect(uri:"/admin/os/list", absolute:true)
		else
			[os: os]
	}
	
	/**
	 * Saves OS changes made by the user. Redirects to index when finished
	 */
	def saveEdit(){
		if(params.name&&params.configurer){
			try{
				def os = OperatingSystem.get(params.id)
				if(os){
					operatingSystemService.setValues(os,params.name,params.configurer)
					flash.message="Operating System values have been modified"
					flash.type="success"
				}
			}catch(Exception e){
				flash.message=e.message
			}
			redirect(uri:"/admin/os/list", absolute:true)
		}else{
			flash.message="All fields are required"
			redirect(uri:"/admin/os/edit/"+params.id, absolute:true)
		}
	}

}
