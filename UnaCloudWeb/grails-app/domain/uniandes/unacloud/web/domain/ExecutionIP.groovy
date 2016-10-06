package uniandes.unacloud.web.domain

import uniandes.unacloud.web.domain.IP;
import uniandes.unacloud.share.enums.IPEnum;

/**
 * Entity to represent an IP address that is used for an Execution
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
