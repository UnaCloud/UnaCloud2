package uniandes.unacloud.web.domain

/**
 * Entity to represent an id in external cloud provider.
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
	Image image
	
	/**
	 * In which provider is located
	 */
	static belongsTo = [provider: ExternalCloudProvider]

    static constraints = {
    }
}
