package unacloud

import grails.transaction.Transactional

@Transactional
class UserGroupService {

    //-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user services
	 */
	
	UserService userService
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Add a user to a group
	 * @param group to add user
	 * @param user 
	 * @return
	 */
	def addToGroup(UserGroup group, User user){
		if(!group.users)group.users =[]
		group.users.add(user)
		group.save(failOnError:true)
	}
	
	/**
	 * Method to return admin default group
	 * if group doesn't exist, create and return it
	 * @return admin default group
	 */
	def UserGroup getAdminGroup(){
		UserGroup admins = UserGroup.findByName(Constants.ADMIN_GROUP);
		if(!admins){
			admins = new UserGroup (name:Constants.ADMIN_GROUP);
			admins.users = []
			admins.save();
		}
		return admins;
	}
		
	
	/**
	 * Method to return users default group
	 * if group doesn't exist, create and return it
	 * @return user default group
	 */
	def UserGroup getDefaultGroup(){
		UserGroup usersGroup = UserGroup.findByName(Constants.USERS_GROUP);
		if(!usersGroup){
			usersGroup = new UserGroup(name:Constants.USERS_GROUP);
			usersGroup.users = []
			usersGroup.save();
		}
		return usersGroup;
	}
	/**
	 * Method to valid if a user is part of the administrators group
	 * @param user: to be valid
	 * @return true is admin, false is not
	 */
	def boolean isAdmin(User user){
		return getAdminGroup().users.find{it.id == user.id}?true:false;
	}
}
