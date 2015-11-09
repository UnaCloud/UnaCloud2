package unacloud

import unacloud.IP;
import unacloud.enums.IPEnum;

/**
 * @author Cesar
 *
 * Representation of IP to use in virtual machines
 */
class ExecutionIP extends IP{
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * State of IP
	 */
	IPEnum state = IPEnum.AVAILABLE
	
	/**
	 * IP Pool to which IP belongs
	 */
	static belongsTo = [ipPool: IPPool]

    static constraints = {
    }
}
