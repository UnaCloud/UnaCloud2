package unacloudservices

import unacloudEnums.VirtualMachineExecutionStateEnum;
import back.services.BackPersistenceService;

class VirtualMachineStateController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of back persistence service
	 */
	
	BackPersistenceService backPersistenceService
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes a virtual machine state validation and renders a response
	 */
	
    def updateVirtualMachineState() {
		String executionId=params['executionId']
		def stateString=params['state']
		def message=params['message']
		if(executionId!=null&&stateString!=null&&message!=null&&executionId.matches("[0-9]+")){
			backPersistenceService.updateVirtualMachineState(Long.parseLong(executionId),VirtualMachineExecutionStateEnum.valueOf(stateString),message)
		}
		render "successful"
	}
}
