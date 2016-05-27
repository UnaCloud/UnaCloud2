package unacloud

import java.util.ArrayList;

import com.losandes.enums.VirtualMachineExecutionStateEnum;


/**
 * Entity to represent a image that has been deployed in infrastructure,
 * this entity has a relationship with deployment and executions that has been requested to be executed..
 * @author CesarF
 *
 */
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
	
	/**
	 * When virtual machine image is deleted, deployment history don't be. Therefore we allow image as nullable.
	 */
	static constraints = {
		image nullable:true
    }
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Returns the list of executions with status not equal FINISHED
	 * @return list of active executions
	 */
	def getActiveExecutions(){
		return virtualMachines.findAll{it.status !=VirtualMachineExecutionStateEnum.FINISHED}.sort{it.id}
	}
	
	/**
	 * Returns the current hardware profile configured in executions
	 * @return hardware profile from first execution
	 */
	def getDeployedHarwdProfile(){
		return virtualMachines.first().getHardwareProfile()
	}
	
	/**
	 * Returns the hostname base for all deployments
	 * @return String with the host name
	 */
	def getDeployedHostname(){
		def ip = virtualMachines.first().mainIp().ip.split('\\.')
		return virtualMachines.first().getName().substring(0, virtualMachines.first().getName().length()-(ip[2].length()+ip[3].length()))
	}
	
	/**
	 * Returns database id for this entity
	 * @return Long id
	 */
	def Long getDatabaseId(){
		return id;
	}
}
