package unacloud

import unacloud.utils.Hasher;
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
			admins = new UserGroup (name:Constants.ADMIN_GROUP, visualName:Constants.ADMIN_GROUP);
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
			usersGroup = new UserGroup(name:Constants.USERS_GROUP, visualName:Constants.USERS_GROUP);
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
	
	/**
	 * Saves a new group with the given users
	 * @param g new empty group
	 * @param users list of users that will belong to the group
	 */
	def addGroup(name, users){
		Date d = new Date()
		def group = new UserGroup(visualName:name, name: "userg"+d.getDate()+"_"+Hasher.randomString(10));		
		group.users = []
		if(users.getClass().equals(String))
			group.users.add(User.findByUsername(users))
		else{
			for(username in users){
				group.users.add(User.findByUsername(username))
			}
		}
		group.save()
	}
}
