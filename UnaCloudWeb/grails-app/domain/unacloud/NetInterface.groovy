package unacloud

/**
 * Entity to represent a Network Interface to be configured in a Virtual Machine.
 * Although this class is used currently system doesn't support more than one configured IP
 * @author CesarF
 *
 */
class NetInterface {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * IP assign in this interface
	 */
	ExecutionIP ip
	
	/**
	 * name of the interface
	 */
	String name
	
	/**
	 * Virtual Execution where this interface is configured
	 */	
	static belongsTo = [virtualExecution:VirtualMachineExecution]

    static constraints = {
    }
}
