package unacloud

import unacloud.IP;
import unacloud.share.enums.IPEnum;

/**
 * Entity to represent an IP address that is used for a Virtual Machine
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
