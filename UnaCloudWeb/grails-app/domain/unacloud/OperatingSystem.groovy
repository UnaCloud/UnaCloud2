package unacloud

class OperatingSystem {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * operating system name
	 */
	String name
	
	/**
	 * operating system configuration class 
	 */
	String configurer
	
    static constraints = {
		name nullable:false, unique:true
    }
}
