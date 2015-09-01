package unacloud2

import unacloud2.enums.ExternalCloudTypeEnum;

class ExternalCloudProvider {
	
	String name
	
	String endpoint
	
	ExternalCloudTypeEnum type
	
	static hasMany = [hardwareProfiles: HardwareProfile, accounts:ExternalCloudAccount]
		
    static constraints = {
    }
}
