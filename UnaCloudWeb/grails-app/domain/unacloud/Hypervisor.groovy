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
	
    static constraints = {
		name nullable:false
    }
}
