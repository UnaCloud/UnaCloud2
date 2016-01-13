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
	
	/**
	 * Return the list of executions with status FINISHED or FAILED
	 * @return
	 */
	def getActiveExecutions(){
		return virtualMachines.findAll{it.status !=VirtualMachineExecutionStateEnum.FINISHED}.sort{it.id}
	}
	
	/**
	 * Return the current hardware profile configured in executions
	 * @return
	 */
	def getDeployedHarwdProfile(){
		return virtualMachines.first().getHardwareProfile()
	}
	
	/**
	 * Return the current hardware profile configured in executions
	 * @return
	 */
	def getDeployedHostname(){
		def ip = virtualMachines.first().mainIp().ip.split('\\.')
		return virtualMachines.first().getName().substring(0, virtualMachines.first().getName().length()-(ip[2].length()+ip[3].length()))
	}
	
	/**
	 * Returns database id
	 * @return
	 */
	def Long getId(){
		return id;
	}
}
