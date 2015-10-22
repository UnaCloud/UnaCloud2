package unacloud

import unacloud.UserService;
import unacloud.enums.UserStateEnum;

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
			if(user.status==UserStateEnum.AVAILABLE){				
				session.user = user
				flash.message=user.name
				redirect(uri: '/', absolute: true)
			}else{
				flash.message="Disabled user"
				redirect(uri: '/login', absolute: true)
			}
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
	 * render create view
	 * @return
	 */
	def create(){
		
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
	
	/**
	 * Deletes the selected user. Redirects to user list when finished.
	 */	
	def delete(){		
		def user = User.get(params.id)
		if (user&&user.id!=session.user.id&&user.status!=UserStateEnum.BLOCKED) {
			try{
				userService.deleteUser(user)
				flash.message="The request will be processed in a few time"
				flash.type="info"
			}catch(Exception e){
				flash.message=e.message
				redirect(uri:"/admin/user/list", absolute:true)
			}
		}
		redirect(uri:"/admin/user/list", absolute:true)		
	}
	
	/**
	 * User edition form action.
	 * @return selected user
	 */
	
	def edit(){
		def user = User.get(params.id)
		if (user&&user.id!=session.user.id) 
			[user: user]
		else
		    redirect(uri:"/admin/user/list", absolute:true)
	}
	
	/**
	 * Saves user's information changes. Redirects to user list when finished.
	 */
	
	def saveEdit(){
		def user = User.get(params.id)
		if(params.name&&params.username&&params.description){
			if(!params.passwd||(params.passwd&&params.passwd.equals(params.cpasswd))){
				if (user&&user.id!=session.user.id){		
					try{						
						userService.setValues(user,params.username, params.name, params.description, params.password)
						flash.message="The user has been modified"
						flash.type="success"
					}catch(Exception e){
						flash.message=e.message
					}		
				}
			}else flash.message="Password fields don't match"			
		}else flash.message="All fields are required"		
		redirect(uri:"/admin/user/list", absolute:true)
	}
	
	
}