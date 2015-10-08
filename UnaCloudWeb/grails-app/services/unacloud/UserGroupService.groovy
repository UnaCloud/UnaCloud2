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
	 * Saves a new group with the given users
	 * @param g new empty group 
	 * @param users list of users that will belong to the group
	 */
    def addGroup(UserGroup g, users){
		g.users = []
		if(users.getClass().equals(String))
			g.users.add(User.findByUsername(users))
		else{
			for(username in users){
				g.users.add(User.findByUsername(username))
			}
		}
		g.save()
    }
	
	/**
	 * Deletes the given group
	 * @param g group to be deleted
	 */
	
	def deleteGroup(UserGroup g){
		g.delete()
	}
	
	/**
	 * Set a new user restriction to all group members
	 * @param g group which users will have the new restriction
	 * @param name restriction name
	 * @param value restriction value
	 */
	
	def setPolicy(UserGroup g, String name, String value){
		for(user in g.users){
			userService.setPolicy(user, name, value)
		}
	}
	
	/**
	 * Edit the given group with new values
	 * @param group group to be edited
	 * @param users new list of users
	 * @param name new name
	 */
	
	def setValues(UserGroup group, users,String name){
		group.putAt("name", name)
		Set newUsers= []
		if(users.getClass().equals(String))
			newUsers.add(User.findByUsername(users))		
		else{
			for(username in users){
				newUsers.add(User.findByUsername(username))
			}
		}
		group.putAt("users", newUsers)		
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
		return getAdminGroup().users.find{it.id = user.id}?true:false;
	}
}
