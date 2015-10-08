package unacloud


/**
 * 
 * @author Cesar
 * 
 * Representation of external id in provider
 */
class ExternalId {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	/**
	 * Id in provider
	 */
	String value
	/**
	 * Image that is saved in provider with this id
	 */
	VirtualMachineImage image
	
	/**
	 * In which provider is located
	 */
	static belongsTo = [provider: ExternalCloudProvider]

    static constraints = {
    }
}
