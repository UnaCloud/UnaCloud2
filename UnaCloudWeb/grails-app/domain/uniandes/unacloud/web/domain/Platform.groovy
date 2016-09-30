package uniandes.unacloud.web.domain

/**
 * Entity to represent a Hypervisor Type 2 installed in physical machines.
 * This class is unused in this version of UnaCloud
 * @author CesarF
 *
 */
class Platform {
	
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
	
	/**
	 * extension list of files for this hypervisor
	 * string with commas
	 * not include main
	 */
	String filesExtensions
	
    static constraints = {
		name nullable:false
		filesExtensions nullable:true
    }
}
