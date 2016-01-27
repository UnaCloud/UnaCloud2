package unacloud

class Hypervisor {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * hypervisor name
	 */
	String name
	
	/**
	 * hypervisor version
	 */
	String hypervisorVersion
	
	/**
	 * extension from main file
	 */
	String mainExtension
	
    static constraints = {
		name nullable:false
    }
}
