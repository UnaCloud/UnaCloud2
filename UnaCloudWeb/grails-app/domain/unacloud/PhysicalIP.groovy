package unacloud

/**
 * @author Cesar
 *
 * Representation of IP from a physical machine
 */
class PhysicalIP extends IP{
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * machine: Physical machine where IP is configured
	 */
	static belongsTo = [machine:PhysicalMachine]

    static constraints = {
    }
}
