package uniandes.unacloud.web.domain

/**
 * Entity to represent an operating system supported by UnaCloud.
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
