package unacloud

/**
 * Entity to represent an Operating System that is supported by UnaCloud.
 *  
 * @author CesarF
 *
 */
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
