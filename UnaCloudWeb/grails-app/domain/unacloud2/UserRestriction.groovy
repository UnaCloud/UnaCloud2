package unacloud2

class UserRestriction {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Restriction name
	 */
	String name
	
	/**
	 * Restriction value
	 */
	String value
	
	/**
	 * Owner
	 */
	static belongsTo = [user: User]
	
    static constraints = {
    }
}
