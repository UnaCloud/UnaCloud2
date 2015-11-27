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
}
