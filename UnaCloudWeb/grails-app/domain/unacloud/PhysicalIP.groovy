package unacloud

/**
 * Entity to represent an IP which is assign to Physical Machine
 * This entity extends from IP
 * @author CesarF
 *
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
