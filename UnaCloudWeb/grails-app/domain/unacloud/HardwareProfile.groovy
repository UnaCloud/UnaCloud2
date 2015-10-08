package unacloud

/**
 * @author Cesar
 *
 * Representation of HardwareProfile based in external cloud models
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
