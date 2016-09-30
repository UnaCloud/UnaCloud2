package uniandes.unacloud.web.domain

/**
 * Entity to represent a Platform to run images and it is installed in physical machines.
 * @author CesarF
 *
 */
class Platform {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * platform name
	 */
	String name
	
	/**
	 * platform version
	 */
	String platformVersion
	
	/**
	 * extension from main file
	 */
	String mainExtension
	
	/**
	 * extension list of files for this platform
	 * string with commas
	 * not include main
	 */
	String filesExtensions
	
    static constraints = {
		name nullable:false
		filesExtensions nullable:true
    }
}
