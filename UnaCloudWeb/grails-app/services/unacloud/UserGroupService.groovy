package unacloud

import com.losandes.utils.Constants;

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
	 * Method to valid if a user is part of administrators group
	 * @param user: to be valid
	 * @return true is admin, false is not
	 */
	def boolean isAdmin(User user){
		return getAdminGroup().users.find{it.id == user.id}?true:false;
	}
	
	/**
	 * Method to valid if a userId belongs to a user that is part of administrators group
	 * @param userId
	 * @return
	 */
	def boolean isAdmin(long userId){
		println 'Entre'
		User user = User.get(userId)
		if(!user)return false
		return isAdmin(user)
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
	 * Edit the given group with new values
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
		print value
		UserRestriction old = group.restrictions.find{it.name==name}
		println "alloc found: "+old
		if(!old&&value){
			def newRestriction= new UserRestriction(name: name, value: value)
			newRestriction.save(failOnError: true)
			println "alloc created: "+newRestriction
			group.restrictions.add(newRestriction)
			group.save(failOnError: true)
		}
		else{
			println "setting value on old: "+old
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
}
