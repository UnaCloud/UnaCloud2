package unacloud

import unacloud.IP;
import unacloud.enums.IPEnum;

/**
 * Entity to represent an ExecutionIP.
 * An Execution IP is a kind of IP that is used for a Virtual Machine
 * 
 * @author CesarF
 */
class ExecutionIP extends IP{
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * State of IP
	 * USED,RESERVED,DISABLED,AVAILABLE
	 */
	IPEnum state = IPEnum.AVAILABLE
	
	/**
	 * IP Pool to which IP belongs
	 */
	static belongsTo = [ipPool: IPPool]

    static constraints = {
    }
}
