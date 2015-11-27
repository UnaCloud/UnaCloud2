package unacloud

class UserRestriction {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Restriction name
	 */
	String name
	
	/**
	 * Restriction value
	 */
	String value
	
	
    static constraints = {
    }
	
	def getValues(){
		return value.isEmpty()?[]:value.split(',')
	}
}
