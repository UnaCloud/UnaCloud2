package unacloud

import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.UserRestrictionEnum;
import grails.transaction.Transactional

@Transactional
class UserRestrictionService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of Hardware profiles services
	 */
	
	HardwareProfileService hardwareProfileService
	
	/**
	 * Representation of Laboratory services
	 */
	
	LaboratoryService laboratoryService
	
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Return the list of valid Hardware Profiles that can be used by user
	 * @param user
	 * @return
	 */
    def getAvoidHwdProfiles(User user) {
		UserRestriction restriction = user.getRestriction(UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES)
		if(!restriction){
			def groups = user.getGroupsWithRestriction(UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES)
			String listProfiles = ""
			for(UserGroup group in groups)
				listProfiles+=group.getRestriction(UserRestrictionEnum.HARDWARE_PROFILE_AVAILABLES).value+(group.equals(groups[groups.size()-1])?"":",")	
			return hardwareProfileService.getHardwareProfilesByName(listProfiles.isEmpty()?null:listProfiles.split(","))
		}else
			return hardwareProfileService.getHardwareProfilesByName(restriction.getValues())
    }
	
	
	def getAvoidLabs(User user){
		UserRestriction restriction = user.getRestriction(UserRestrictionEnum.ALLOWED_LABS)
		if(!restriction){
			def groups = user.getGroupsWithRestriction(UserRestrictionEnum.ALLOWED_LABS)
			String listLabs = ""
			for(UserGroup group in groups)
				listLabs+=group.getRestriction(UserRestrictionEnum.ALLOWED_LABS).value+(group.equals(groups[groups.size()-1])?"":",")
			return laboratoryService.getLabsByName(listLabs.isEmpty()?null:listLabs.split(','))
		}else
			return laboratoryService.getLabsByName(restriction.getValues())
	}
}
