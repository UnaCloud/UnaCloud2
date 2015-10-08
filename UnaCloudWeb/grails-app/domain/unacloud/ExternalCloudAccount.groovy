package unacloud

/**
 * 
 * @author Cesar
 * Representation of Account in cloud provider
 */
class ExternalCloudAccount {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	/**
	 * Name of account
	 */
	String name
	
	/**
	 * provider: External Cloud Provider
	 * owner: user owner of this account
	 */
	static belongsTo = [provider:ExternalCloudProvider, owner: User]
	
	/**
	 * Account id in cloud provider
	 */
	String account_id
	
	/**
	 * Account key in cloud provider
	 */
	String account_key
	
	/**
	 * Name of the repository to save files
	 */
	String bucketName
	
    static constraints = {
		name unique: true
		bucketName nullable: true
    }
}
