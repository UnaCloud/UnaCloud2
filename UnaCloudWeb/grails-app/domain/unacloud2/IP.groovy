package unacloud2

class IP {

	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * IP representation
	 */
    String ip
	
	/**
	 * Indicates if this IP is in use or not
	 */
	boolean used
	
	/**
	 * IP Pool to which IP belongs
	 */
	IPPool ipPool
	
	
	static constraints = {
		ipPool nullable:true
		ip unique: true	
	}
}
