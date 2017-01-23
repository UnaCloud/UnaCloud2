package uniandes.unacloud.web.domain

import uniandes.unacloud.share.enums.IPEnum;

/**
 * Entity to represent an IP address.
 * The purpose of this entity is to be extended by other kinds of IP
 * @author CesarF
 *
 */
class IP {

	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * IP representation
	 */
    String ip		
	
	/**
	 * IP Should be unique in database
	 */
	static constraints = {
		ip unique: true	
	}
}
