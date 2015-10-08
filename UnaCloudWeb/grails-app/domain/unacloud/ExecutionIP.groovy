package unacloud

import unacloud.IP;

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
	 * Indicates if this IP is in use or not
	 */
	boolean used
	
	/**
	 * IP Pool to which IP belongs
	 */
	static belongsTo = [ipPool: IPPool]

    static constraints = {
    }
}
