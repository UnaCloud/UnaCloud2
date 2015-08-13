package unacloud2

class Repository {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Repository name
	 */
	String name
	
	/**
	 * Repository capacity in GB
	 */
	int capacity
	
	/**
	 * Repository path
	 */
	String root
	
	/**
	 * Images stored in this repository
	 */
	static hasMany = [images: VirtualMachineImage]
	
    static constraints = {
	}
	
}
