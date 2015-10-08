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
	 * list of users that belongs to this group
	 * List of restrictions that apply for all users
	 */
	static hasMany = [users: User, restrictions: UserRestriction]
	
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	
	
}
