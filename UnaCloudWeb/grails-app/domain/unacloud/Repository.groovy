package unacloud

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
	String path
	
	/**
	 * Images stored in this repository
	 */
	static hasMany = [images: VirtualMachineImage]
	
    static constraints = {
	}
	
	/**
	 *
	 * @return true is the repository is default, false is not
	 */
	
	def boolean isDefault(){
		return this == Repository.findByName(Constants.MAIN_REPOSITORY);		
	}
}
