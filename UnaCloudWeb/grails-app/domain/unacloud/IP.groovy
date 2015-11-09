package unacloud

import unacloud.enums.IPEnum;

/**
 * @author Cesar
 * 
 * Representation of IP
 */
class IP {

	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * IP representation
	 */
    String ip		
	
	
	static constraints = {
		ip unique: true	
	}
}
