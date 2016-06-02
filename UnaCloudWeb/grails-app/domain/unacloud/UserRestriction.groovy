package unacloud

/**
 * Entity to represent a restriction for a group or user. Currently UnaCloud supports restrictions to deploy virtual machines: allocation algorithm, available laboratories and available hardware profiles. 
 * @author CesarF
 *
 */
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
	 * in case of multiple it save only one string separated by commas
	 */
	String value
	
	
    static constraints = {
    }
	
	/**
	 * Return values in restriction
	 * @return the list of values in restriction split by comma
	 */
	def getValues(){
		return value.isEmpty()?[]:value.split(',')
	}
}
