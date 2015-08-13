package unacloud2

class Grupo {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
		
	/**
	 * group name
	 */
	String name
	
	/**
	 * list of users that belongs to this group
	 */
	static hasMany = [users: User]
}
