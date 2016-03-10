package unacloud

/**
 * Entity to represent a User Restriction
 * An UserRestriction is a restriction for a group or user, like allocator, laboratories and hardware profiles. 
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
