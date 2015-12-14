package unacloud

import java.util.ArrayList;

import unacloud.enums.VirtualMachineExecutionStateEnum;

class DeployedImage {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * representation of the virtual machine image
	 */
	VirtualMachineImage image
	
	/**
	 * it tells if the image is set to be deployed in high availability machines
	 */
	boolean highAvaliavility
	
	/**
	 * list of deployed nodes from the image
	 */
	static hasMany = [virtualMachines: VirtualMachineExecution]
	
	/**
	 * Representation of deployed cluster 
	 */
    static belongsTo = [deployment: Deployment]
	
	static constraints = {
		image nullable:true
    }
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	def getActiveExecutions(){
		return virtualMachines.findAll{it.status !=VirtualMachineExecutionStateEnum.FINISHED}.sort{it.id}
	}
}
