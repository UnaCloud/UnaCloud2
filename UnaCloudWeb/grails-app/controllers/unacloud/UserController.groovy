package unacloud

import unacloud.UserService;

class UserController {
	
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
	
	def beforeInterceptor = [action:{
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
		}, except: [
			'login',
			'logout',
			'home'
		]]
	
	/**
	 * Default action
	 */
	def index() {
		redirect(uri:"/error", absolute:true)
	}
	
	/**
	 * User list action
	 * @return List of all users
	 */
	def list(){
		[users: User.list().sort{it.id}];
	}
	
	/**
	 * Validates passwords and changes session user. Redirects to home page
	 * if credentials are correct, or to login page if they're not
	 */
	
	def login(){
		def user = userService.getUser(params.username,params.password)
		println user
		if (user){
			session.user = user
			flash.message=user.name
			redirect(uri: '/', absolute: true)
		}
		else {
			flash.message="Wrong username or password"
			redirect(uri: '/login', absolute: true)
		}
	}
	
	/**
	 * Removes the current session user. Redirects to login page
	 * @return
	 */
	def logout(){
		session.invalidate()
		redirect(uri: '/login', absolute: true)
	}
	
	/**
	 * Home action. Selects redirection depending on session status and privileges
	 */
	
	def home(){
		if(!session.user){
			flash.message="Your session has expired"
			redirect(uri:"/login", absolute:true)
			return false
		}else{
			User user = session.user;		
		}		
	}	

	/**
	 * Create a new user
	 */
	def save(){
		if(params.name&&params.username&&params.passwd&&params.description){	
			try{
				userService.addUser(params.username, params.name,params.description,params.passwd)
				redirect(uri:"/admin/user/list", absolute:true)
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/user/new", absolute:true)
			}		
		}else{
			flash.message="All fields are required"					
			redirect(uri:"/admin/user/new", absolute:true)
		}	
	}
}