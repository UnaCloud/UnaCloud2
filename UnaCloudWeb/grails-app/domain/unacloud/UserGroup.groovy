package unacloud

class UserGroup {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
		
	/**
	 * group name
	 */
	String name
	
	/**
	 * Name to be modified
	 */
	String visualName
	
	/**
	 * list of users that belongs to this group
	 * List of restrictions that apply for all users
	 */
	static hasMany = [users: User, restrictions: UserRestriction]
	
	static constraints = {
		name unique: true, nullable: false
	}
	
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Validate if group is admin group
	 * @return
	 */
	def boolean isAdmin(){
		return name.equals(Constants.ADMIN_GROUP)
	}
	
	/**
	 * Validate if group is user default group
	 * @return
	 */
	def boolean isDefault(){
		return name.equals(Constants.USERS_GROUP)
	}
	
}
