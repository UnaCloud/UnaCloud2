package unacloud

/**
 * Entity to represent an external id in cloud provider.
 * This class is unused in this version of UnaCloud
 * @author CesarF
 *
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
