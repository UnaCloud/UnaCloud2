package unacloud

/**
 * Entity to represent a Hardware Profile. Default values are based in external cloud providers.
 * @author CesarF
 *
 */
class HardwareProfile {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Name of hardware profile
	 */
	String name
	/**
	 * Number of cores
	 */
	int cores
	/**
	 * Ram in MB
	 */
	int ram
	
    static constraints = {
    }
}
