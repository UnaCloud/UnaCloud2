package unacloud

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
