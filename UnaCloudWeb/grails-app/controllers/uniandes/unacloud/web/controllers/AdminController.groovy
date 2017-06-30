package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.pmallocators.AllocatorEnum;
import uniandes.unacloud.web.services.HardwareProfileService;
import uniandes.unacloud.web.services.LaboratoryService;
import uniandes.unacloud.web.services.UserGroupService;
import uniandes.unacloud.web.services.UserService;
import uniandes.unacloud.share.enums.UserRestrictionEnum;
import uniandes.unacloud.share.enums.UserStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.utils.groovy.UserSession;


/**
 * This Controller contains actions to manage Users by Admin User: User crud, set restrictions .
 * This class render pages or process requests in services to update entities, there is session verification before all methods to verify that only admin could use services.
 * @author CesarF
 *
 */
class AdminController {

   
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
	
	/**
	 * Representation of Hardware profiles services
	 */
	
	HardwareProfileService hardwareProfileService
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing user administration actions
	 */	
	def beforeInterceptor = [action:{
			if (!session.user) {
				flash.message = "You must log in first"
				redirect(uri:"/login", absolute:true)
				return false
			}
			else {
				def user = User.get(session.user.id)
				session.user.refresh(user)
				if (!userGroupService.isAdmin(user)) {
					flash.message = "You must be administrator to see this content"
					redirect(uri:"/error", absolute:true)
					return false
				}
			}
		}]
	
	
	/**
	 * User list action
	 * @return List of all users
	 */
	def list() {
		[users: User.list().sort{it.id}];
	}	
	
	/**
	 * renders create view
	 * @return
	 */
	def create() {
		
	}

	/**
	 * Creates a new user
	 */
	def save() {
		if (params.name && params.username && params.passwd && params.description) {	
			try {
				userService.addUser(params.username, params.name, params.description, params.passwd, params.email)
				redirect(uri:"/admin/user/list", absolute:true)
			} catch(Exception e) {
				flash.message = e.message
				redirect(uri:"/admin/user/new", absolute:true)
			}		
		} else {
			flash.message = "All fields are required"					
			redirect(uri:"/admin/user/new", absolute:true)
		}	
	}
	
	/**
	 * Deletes the selected user. Redirects to user list when finished.
	 */	
	def delete() {		
		def user = User.get(params.id)
		def admin = User.get(session.user.id)
		if (user && user.id != admin.id && user.status != UserStateEnum.BLOCKED) {
			try {
				userService.deleteUser(user, admin)
				flash.message = "The request will be processed in a few time"
				flash.type = "info"
			} catch(Exception e) {
				e.printStackTrace()
				flash.message = e.message
			}
		}
		redirect(uri:"/admin/user/list", absolute:true)		
	}
	
	/**
	 * User edition form action.
	 * @return selected user
	 */	
	def edit() {
		def user = User.get(params.id)
		if (user && user.id != session.user.id) 
			[user: user]
		else
		    redirect(uri:"/admin/user/list", absolute:true)
	}
	
	/**
	 * Saves user's information changes. Redirects to user list when finished.
	 */	
	def saveEdit() {
		def user = User.get(params.id)
		if (params.name && params.username && params.description)
			if (!params.passwd || (params.passwd && params.passwd.equals(params.cpasswd)))
				if (user && user.id != session.user.id) {		
					try{						
						userService.setValues(user,params.username, params.name, params.description, params.passwd, params.email)
						flash.message = "The user has been modified"
						flash.type = "success"
					} catch(Exception e) {
						flash.message = e.message
					}		
				}
			else flash.message = "Password fields don't match"			
		else flash.message = "All fields are required"		
		redirect(uri:"/admin/user/list", absolute:true)
	}
	
	/**
	 * Renders page to edit restrictions
	 * 
	 */
	def config(){
		def user = User.get(params.id)
		if (!user)
			redirect(uri:"/admin/user/list", absolute:true)
		else
			[user: user.id, restrictions:[
				  [name: UserRestrictionEnum.ALLOCATOR.name, type:UserRestrictionEnum.ALLOCATOR.toString(), list:true, current:user.getRestriction(UserRestrictionEnum.ALLOCATOR), values:AllocatorEnum.getList(), multiple:false],
				  [name: UserRestrictionEnum.ALLOWED_LABS.name, type:UserRestrictionEnum.ALLOWED_LABS.toString(), list:true, current:user.getRestriction(UserRestrictionEnum.ALLOWED_LABS), values:laboratoryService.getLabsNames(),multiple:true],
				  [name: UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.name, type:UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(), list:true, current:user.getRestriction(UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES), values:hardwareProfileService.getProfilesNames(), multiple:true]
				  //This line is only for example: how to create not list not multiple
				  // [name:UserRestrictionEnum.MAX_RAM_PER_VM.name,type:UserRestrictionEnum.MAX_RAM_PER_VM.toString(), list:false,current:user.getRestriction(UserRestrictionEnum.MAX_RAM_PER_VM),multiple:false]	
				]
			]
		
	}
	
	/**
	 * Sets restrictions of user
	 */
	def setRestrictions() {
		def user = User.get(params.id)
		if (!user)
			redirect(uri:"/admin/user/list", absolute:true)
		else {
			def modify = false
			if (params.restriction) {
				def value = params.value	
				if (UserRestrictionEnum.getRestriction(params.restriction) == UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES) {
					if (value.getClass().equals(String))
						userService.setRestriction(user,UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(), value)
					else {
						String list = ""
						for (hwdp in params.value)
							list += hwdp + (hwdp.equals(params.value[params.value.size()-1]) ? "" : ",")
						userService.setRestriction(user, UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(), list)
					}
					modify = true					
				} else if (UserRestrictionEnum.getRestriction(params.restriction) == UserRestrictionEnum.ALLOWED_LABS) {
					if (value.getClass().equals(String))
						userService.setRestriction(user, UserRestrictionEnum.ALLOWED_LABS.toString(),value)
					else {
						String list = ""
						for (lab in params.value)
							list += lab + (lab.equals(params.value[params.value.size()-1]) ? "" : ",")
						userService.setRestriction(user, UserRestrictionEnum.ALLOWED_LABS.toString(), list)
					}
					modify = true
				} else if (UserRestrictionEnum.getRestriction(params.restriction) == UserRestrictionEnum.ALLOCATOR) {
					def allocator = AllocatorEnum.getAllocatorByName(value)
					userService.setRestriction(user, UserRestrictionEnum.ALLOCATOR.toString(), allocator ? allocator.getName() : null)	
					modify = true
				}		
			}	
			if (modify) {
				flash.message = "User restrictions have been modified"
				flash.type = "success"
			}
			redirect(uri:"/admin/user/restrictions/" + user.id, absolute:true)
		}
	}
}
