package unacloud

import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.UserRestrictionEnum;
import unacloud.pmallocators.AllocatorEnum;
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
	
	/**
	 * Representation of Server Variable services
	 */
	
	ServerVariableService serverVariableService
	
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
	
	/**
	 * Return the list of valid Laboratories to deploy virtual executions
	 * @param user
	 * @return
	 */
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
	
	/**
	 * Return the allocator configured in restrictions for user, in case there is not configured an
	 * allocator restriction for user it takes default allocator in server variable
	 * @param user
	 * @return
	 */
	def getAllocator(User user){
		UserRestriction restriction = user.getRestriction(UserRestrictionEnum.ALLOCATOR)
		if(!restriction){
			def groups = user.getGroupsWithRestriction(UserRestrictionEnum.ALLOCATOR)
			if(groups.size()>0)
				return AllocatorEnum.getAllocatorByName(groups.get(0).getRestriction(UserRestrictionEnum.ALLOCATOR).value)				
			return AllocatorEnum.getAllocatorByName(serverVariableService.getDefaultAllocator().variable);
		}else
			return AllocatorEnum.getAllocatorByName(restriction.value)
	}
}
