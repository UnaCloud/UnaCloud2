package unacloud2

class GroupController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of group services
	 */
	
	GroupService groupService
	
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
	 * Index action
	 * @return list with all groups 
	 */
	
	def index() {
		[groups: Grupo.list(params)]
	}
	
	/**
	 * Create group form action
	 * @return list with all user for group creation
	 */
	def create(){
		[users: User.list(params)]
	}
	
	/**
	 * Save group action. Redirects to group index when finished.
	 */
	def add(){
		def group= new Grupo(name:(params.name))
		def users= params.users
		groupService.addGroup(group, users)
		redirect(controller:"group" ,action:"index")
	}
	
	/**
	 * Delete group action. Redirects to index when finished
	 */
	def delete() {
		def group = Grupo.findByName(params.name)
		if (!group) {
		redirect(action:list)
		}
		
		else{
		groupService.deleteGroup(group)
		redirect(controller:"group" ,action:"index")
	
		}
	}
	
	/**
	 * Set user restrictions for all group members
	 * Redirects to index when finished 
	 */
	def setPolicy(){
		Grupo g= Grupo.findByName(params.name)
		println "Grupo:"+g
		groupService.setPolicy(g, params.type,  params.value)
		redirect(action:"index")
	}
	
	/**
	 * set user restrictions form action 
	 * @return group selected for restrictions edition
	 */
	def editPerms(){
		def g= Grupo.findByName(params.name)
		if (!g) {
			redirect(action:"index")
		}
		[group: g]		
	}
	
	/**
	 * Edit group form action
	 * @return list of users and group selected for edition
	 */
	def edit(){
		def g= Grupo.findByName(params.name)		
		if (!g)
		redirect(action:"index")
		else
		[users: User.list(params), group:g]
	}
	
	/**
	 * edit values action. Receives new group information and sends it to service 
	 * Redirects to index when finished
	 */
	def setValues(){
		System.out.println(params.oldName)
		def group = Grupo.findByName(params.oldName)
		groupService.setValues(group, params.users,params.name)
		redirect(action:"index")
	}
}
