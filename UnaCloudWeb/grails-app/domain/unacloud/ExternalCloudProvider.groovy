package unacloud

import unacloud.enums.ExternalCloudTypeEnum;

/**
 * Entity to represent a external cloud provider.
 * This class is unused in this version of UnaCloud
 * @author CesarF
 *
 */
class ExternalCloudProvider {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Name of the provider
	 */
	String name
	
	String endpoint
	
	/**
	 * Type of provider (COMPUTING, STORAGE)
	 */
	ExternalCloudTypeEnum type
	
	/**
	 * hardwareProfiles: list of hardwareProfiles to deploy machines in case of Type is Computing
	 * 
	 * accounts: list of accounts used by users
	 */
	static hasMany = [hardwareProfiles: HardwareProfile, accounts:ExternalCloudAccount]
		
    static constraints = {
    }
}
