package uniandes.unacloud.web.controllers

import uniandes.unacloud.web.services.HardwareProfileService;
import uniandes.unacloud.web.services.LaboratoryService;
import uniandes.unacloud.web.services.UserGroupService;
import uniandes.unacloud.web.services.UserService;
import uniandes.unacloud.web.pmallocators.AllocatorEnum;
import uniandes.unacloud.share.enums.UserRestrictionEnum;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.UserGroup;

/**
 * This Controller contains actions to manage User Group services: crud and deploy cluster.
 * This class render pages for user or process request in services to update entities, there is session verification before all actions
 * only administrator users can call this actions.
 * @author CesarF
 *
 */
class UserGroupController {

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
	
	def beforeInterceptor = {
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
	}
	
	/**
	 * Index action
	 * @return list with all groups
	 */
	
	def list() {
		[groups: UserGroup.list()]
	}
	
	/**
	 * Creates group form action
	 * @return list with all user for group creation
	 */
	def create() {
		[users: User.list()]
	}
	
	/**
	 * Saves group action. Redirects to group list when finished.
	 */
	def save() {
		if(params.name) {
			try {
				userGroupService.addGroup(params.name, params.users)
				redirect(uri:"/admin/group/list", absolute:true)
			} catch(Exception e) {
				flash.message = e.message
				redirect(uri:"/admin/group/new", absolute:true)
			}
		} else {
			flash.message = "All fields are required"
			redirect(uri:"/admin/group/new", absolute:true)
		}
	}
	
	/**
	 * Deletes the selected group. Redirects to group list when finished.
	 */
	def delete(){
		def group = UserGroup.get(params.id)
		if (!group.isAdmin() && !group.isDefault()) {
			try{
				userGroupService.deleteGroup(group)
				flash.message = "Your request has been processed"
				flash.type = "success"
			} catch(Exception e) {
				flash.message=e.message
			}
		}
		redirect(uri:"/admin/group/list", absolute:true)
	}
	
	/**
	 * Renders a group information to be modified by user
	 * @return list of users and group selected for edition
	 */
	def edit(){
		def group = UserGroup.get(params.id)
		if (!group)
			redirect(uri:"/admin/group/list", absolute:true)
		else
			[users: User.list(), group:group]
	}
	
	/**
	 * edits values action. Receives new group information and sends it to service 
	 * Redirects to group list when finished
	 */
	def saveEdit() {
		if (params.name && params.id) {
			try {
				UserGroup group = UserGroup.get(params.id)
				if (group) {				
					userGroupService.setValues(group, params.users, params.name)
					flash.message = "Group values have been modified"
					flash.type = "success"
				}						
			} catch(Exception e) {
				flash.message = e.message
			}
			redirect(uri:"/admin/group/list", absolute:true)
		} else {
			flash.message = "All fields are required"
			redirect(uri:"/admin/group/edit/" + params.id, absolute:true)
		}
	}
	
	/**
	 * Renders page to edit restrictions
	 *
	 */
	def config(){
		def group = UserGroup.get(params.id)
		if (!group)
			redirect(uri:"/admin/group/list", absolute:true)
		else{
			[group:group.id,restrictions:[
				  [name:UserRestrictionEnum.ALLOCATOR.name, type:UserRestrictionEnum.ALLOCATOR.toString(), list:true, current:group.getRestriction(UserRestrictionEnum.ALLOCATOR), values:AllocatorEnum.getList(), multiple:false],
				  [name:UserRestrictionEnum.ALLOWED_LABS.name, type:UserRestrictionEnum.ALLOWED_LABS.toString(), list:true, current:group.getRestriction(UserRestrictionEnum.ALLOWED_LABS), values:laboratoryService.getLabsNames(), multiple:true],
				  [name:UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.name, type:UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(), list:true, current:group.getRestriction(UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES), values:hardwareProfileService.getProfilesNames(), multiple:true]
		    	]
			]
		}
	}
	
	/**
	 * Sets restrictions of a selected group
	 * @return
	 */
	def setRestrictions(){
		def group = UserGroup.get(params.id)
		if (!group)
			redirect(uri:"/admin/group/list", absolute:true)
		else {
			def modify = false
			if (params.restriction) {
				def value = params.value
				if (UserRestrictionEnum.getRestriction(params.restriction) == UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES) {
					if (value.getClass().equals(String))
						userGroupService.setRestriction(group, UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(),value)
					else {
						String list = ""
						for (hwdp in params.value)
							list += hwdp + (hwdp.equals(params.value[params.value.size()-1]) ? "" : ",")
						userService.setRestriction(group, UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES.toString(), list)
					}
					modify = true
				} else if(UserRestrictionEnum.getRestriction(params.restriction) == UserRestrictionEnum.ALLOWED_LABS) {
					if (value.getClass().equals(String))
						userGroupService.setRestriction(group, UserRestrictionEnum.ALLOWED_LABS.toString(), value)
					else{
						String list = ""
						for (lab in params.value)
							list += lab + ","
						userGroupService.setRestriction(group, UserRestrictionEnum.ALLOWED_LABS.toString(), list)
					}
					modify = true
				} else if(UserRestrictionEnum.getRestriction(params.restriction) == UserRestrictionEnum.ALLOCATOR) {
					def allocator = AllocatorEnum.getAllocatorByName(value)
					userGroupService.setRestriction(group,UserRestrictionEnum.ALLOCATOR.toString(), allocator ? allocator.getName() : null)
					modify = true
				} else if(UserRestrictionEnum.getRestriction(params.restriction) == UserRestrictionEnum.REPOSITORY) {
					//TODO implement
				}
			}
			if (modify) {
				flash.message="Group restrictions have been modified"
				flash.type="success"
			}
			redirect(uri:"/admin/group/restrictions/" + group.id, absolute:true)
		}
	}
}
