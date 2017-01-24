package uniandes.unacloud.web.services

import uniandes.unacloud.common.utils.UnaCloudConstants;

import uniandes.unacloud.web.utils.java.Hasher;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.UserGroup;
import uniandes.unacloud.web.domain.UserRestriction;

import grails.transaction.Transactional

/**
 * This service contains all methods to manage User group and users in groups: UserGroup crud methods, and methods to add and remove user from groups.
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
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
	 * Adds a user to a group
	 * @param group to add user
	 * @param user 
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
		UserGroup admins = UserGroup.findByName(UnaCloudConstants.ADMIN_GROUP);
		if(!admins){
			admins = new UserGroup (name:UnaCloudConstants.ADMIN_GROUP, visualName:UnaCloudConstants.ADMIN_GROUP);
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
		UserGroup usersGroup = UserGroup.findByName(UnaCloudConstants.USERS_GROUP);
		if(!usersGroup){
			usersGroup = new UserGroup(name:UnaCloudConstants.USERS_GROUP, visualName:UnaCloudConstants.USERS_GROUP);
			usersGroup.users = []
			usersGroup.save();
		}
		return usersGroup;
	}
	
	/**
	 * Method to valid if a user is part of administrators group
	 * @param user: to be validate
	 * @return true is admin, false is not
	 */
	def boolean isAdmin(User user){
		return getAdminGroup().users.find{it.id == user.id}?true:false;
	}
	
	/**
	 * Method to valid if a userId belongs to a user that is part of administrators group
	 * @param userId
	 * @return true if user id is from a admin user, false in case not
	 */
	def boolean isAdmin(long userId){
		User user = User.get(userId)
		if(!user)return false
		return isAdmin(user)
	}
	
	/**
	 * Saves a new group with the given users
	 * @param name new name group
	 * @param users list of users that will belong to the group
	 */
	def addGroup(name, users){
		Date d = new Date()
		def group = new UserGroup(visualName:name, name: "userg"+d.getDate()+"_"+Hasher.randomString(10));		
		group.users = []
		if(users.getClass().equals(String))
			group.users.add(User.get(users))
		else{
			for(userId in users){
				group.users.add(User.get(userId))
			}
		}
		group.save()
	}
	
	/**
	 * Deletes the given group
	 * @param group to be deleted
	 */	
	def deleteGroup(UserGroup group){
		for(restriction in group.restrictions)
			restriction.delete()
		group.delete()
	}
	
	/**
	 * Edits the given group with new values
	 * @param group group to be edited
	 * @param users new list of users
	 * @param name new name
	 */	
	def setValues(UserGroup group, users, String name){
		group.putAt("visualName", name)
		Set newUsers= []
		if(users.getClass().equals(String))
			newUsers.add(User.get(users))		
		else{
			for(userId in users){
				newUsers.add(User.get(userId))
			}
		}
		group.putAt("users", newUsers)		
	}
	
	/**
	 * Sets a new group restriction to the given group
	 * @param group with the new restriction
	 * @param name restriction name
	 * @param value restriction value
	 */	
	def setRestriction(UserGroup group, String name, String value){
		UserRestriction old = group.restrictions.find{it.name==name}
		if(!old&&value){
			def newRestriction= new UserRestriction(name: name, value: value)
			newRestriction.save(failOnError: true)
			group.restrictions.add(newRestriction)
			group.save(failOnError: true)
		}
		else{
			if(!value||value.equals("")){
				group.restrictions.remove(old)
				old.delete()
			}
			else{
				old.setValue(value)
				old.save(failOnError: true)
			}
		}
	}
	
	/**
	 * Removes user from all groups where it is added
	 * @param user to be removed
	 */
	def removeUser(User user){
		def groups = UserGroup.where{users{id == user.id}}.findAll()
		for(UserGroup group in groups)group.users.remove(user)
	}
}
