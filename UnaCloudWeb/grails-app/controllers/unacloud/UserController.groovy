package unacloud

import back.pmallocators.AllocatorEnum;
import unacloud.UserService;
import unacloud.enums.UserRestrictionEnum;
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
	
	/**
	 * Representation of labs services
	 */
	
	LaboratoryService laboratoryService
	
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
	
	/**
	 * Render page to edit restrictions
	 * 
	 */
	def config(){
		def user = User.get(params.id)
		if(!user){
			redirect(uri:"/admin/user/list", absolute:true)
		}else{
			[user:user.id,restrictions:[
				  [name:UserRestrictionEnum.ALLOCATOR.name,type:UserRestrictionEnum.ALLOCATOR.toString(),list:true,current:user.getRestriction(UserRestrictionEnum.ALLOCATOR),values:AllocatorEnum.getList(),multiple:false],
				  [name:UserRestrictionEnum.ALLOWED_LABS.name,type:UserRestrictionEnum.ALLOWED_LABS.toString(), list:true,current:user.getRestriction(UserRestrictionEnum.ALLOWED_LABS),values:laboratoryService.getLabsNames(),multiple:true],
				  [name:UserRestrictionEnum.MAX_CORES_PER_VM.name,type:UserRestrictionEnum.MAX_CORES_PER_VM.toString(),list:false,current:user.getRestriction(UserRestrictionEnum.MAX_CORES_PER_VM),multiple:false],
				  [name:UserRestrictionEnum.MAX_RAM_PER_VM.name,type:UserRestrictionEnum.MAX_RAM_PER_VM.toString(), list:false,current:user.getRestriction(UserRestrictionEnum.MAX_RAM_PER_VM),multiple:false]	
				]
			]
		}
	}
	/**
	 * Set restrictions of user
	 * @return
	 */
	def setRestrictions(){
		def user = User.get(params.id)
		if(!user){
			redirect(uri:"/admin/user/list", absolute:true)
		}else{
			def modify = false
			if(params.restriction){
				def value = params.value	
				if(UserRestrictionEnum.getRestriction(params.restriction)==UserRestrictionEnum.MAX_CORES_PER_VM){
					userService.setRestriction(user,UserRestrictionEnum.MAX_CORES_PER_VM.toString(),value)
					modify = true
				}else if(UserRestrictionEnum.getRestriction(params.restriction)==UserRestrictionEnum.MAX_RAM_PER_VM){
					userService.setRestriction(user,UserRestrictionEnum.MAX_RAM_PER_VM.toString(),value)
					modify = true
				}else if(UserRestrictionEnum.getRestriction(params.restriction)==UserRestrictionEnum.ALLOWED_LABS){
					if(value.getClass().equals(String)){
						userService.setRestriction(user,UserRestrictionEnum.ALLOWED_LABS.toString(),value)
					}else{
						String list = ""
						for(lab in params.value)
							list+=lab+","
						userService.setRestriction(user,UserRestrictionEnum.ALLOWED_LABS.toString(),list)
					}
					modify = true
				}else if(UserRestrictionEnum.getRestriction(params.restriction)==UserRestrictionEnum.ALLOCATOR){
					def allocator = AllocatorEnum.getAllocatorByName(value)
					userService.setRestriction(user,UserRestrictionEnum.ALLOCATOR.toString(),allocator?allocator.getName():null)	
					modify = true
				}		
			}	
			if(modify){
				flash.message="User restrictions have been modified"
				flash.type="success"
			}
			redirect(uri:"/admin/user/restrictions/"+user.id, absolute:true)
		}
	}
	
}