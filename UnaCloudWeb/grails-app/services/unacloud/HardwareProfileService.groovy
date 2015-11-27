package unacloud

import grails.transaction.Transactional

@Transactional
class HardwareProfileService {

	/**
	 * Return the lab name list
	 */
    def getProfilesNames() {
		return HardwareProfile.executeQuery("select name from HardwareProfile")
    }
	
	/**
	 * Return all Hardware Profiles searched by names array
	 * @param names list of names 
	 * @return list of Hardware Profiles
	 */
	def getHardwareProfilesByName(String[] names){
		if(names==null)
			return HardwareProfile.all
		return HardwareProfile.where{name in names}.findAll()
	}
}
