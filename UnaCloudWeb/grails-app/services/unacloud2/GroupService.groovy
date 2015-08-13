package unacloud2

class GroupService {
	
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
    def addGroup(Grupo g, users){
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
	
	def deleteGroup(Grupo g){
		g.delete()
	}
	
	/**
	 * Set a new user restriction to all group members
	 * @param g group which users will have the new restriction
	 * @param name restriction name
	 * @param value restriction value
	 */
	
	def setPolicy(Grupo g, String name, String value){
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
	
	def setValues(Grupo group, users,String name){
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
}
