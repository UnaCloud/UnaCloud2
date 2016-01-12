package webutils

import unacloud.User
import unacloud.UserGroupService
import unacloud.UserService

/**
 * Class that represent a user instance to be saved in session.
 * This class has the purpose to avoid duplicated instance and exception
 * @author CesarAugusto
 *
 */
class UserSession {
	
	long id
	String name
	String username
	String description
	String registerDate	
		
	public UserSession(long id, String name, String username, String description, String registerDate){
		this.id = id
		this.name = name
		this.username = username
		this.description = description
		this.registerDate = registerDate		
	}
	
	/**
	 * Validates in user group service if user in session is admin
	 * @return
	 */
	public boolean isAdmin(){
		println 'Session'
		UserGroupService userGroupService = new UserGroupService()
		println userGroupService
		return userGroupService.isAdmin(id)
	}

	public void refresh(){
		UserService userService = new UserService()
		User user = userService.getUser(id)
		this.name = user.name
		this.username = user.username
		this.description = user.description
		this.registerDate = user.registerDate
	}
}
