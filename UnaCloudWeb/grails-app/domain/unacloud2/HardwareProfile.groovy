package unacloud2

/**
 * @author Cesar
 *
 * Representation of HardwareProfile based in external cloud models
 */

class HardwareProfile {
	
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
