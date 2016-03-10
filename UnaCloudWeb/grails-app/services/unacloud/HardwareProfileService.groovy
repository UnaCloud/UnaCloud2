package unacloud

import grails.transaction.Transactional

/**
 * This service contains all methods to manage Hardware profiles: return a list of hardware profiles and query by name filter.
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
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
