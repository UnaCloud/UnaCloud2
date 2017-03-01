package uniandes.unacloud.web.domain

/**
 * Entity to represent a Network Interface to be configured in an execution.
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
	 * Execution where this interface is configured
	 */	
	static belongsTo = [execution:Execution]

    static constraints = {
		ip nullable: true
    }
}
