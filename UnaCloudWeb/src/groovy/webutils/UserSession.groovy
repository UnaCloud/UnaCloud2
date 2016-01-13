package webutils

import unacloud.User
import unacloud.UserGroupService
import unacloud.UserService

/**
 * Class that represent a user instance to be saved in session.
 * This class has the purpose to avoid duplicated instance and exception
 * @author Cesar
 *
 */
class UserSession {
	
	long id
	String name
	String username
	String description
	String registerDate
	boolean isAdmin
		
	public UserSession(long id, String name, String username, String description, String registerDate, boolean admin){
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
		return isAdmin
	}

	/**
	 * Update this entity based in a user entity sent by parameters
	 * @param user
	 */
	public void refresh(User user){
		this.name = user.name
		this.username = user.username
		this.description = user.description
		this.registerDate = user.registerDate
		this.isAdmin = user.isAdmin()
	}
}
