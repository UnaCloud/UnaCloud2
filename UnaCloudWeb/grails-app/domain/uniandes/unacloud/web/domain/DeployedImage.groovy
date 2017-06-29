package uniandes.unacloud.web.domain

import java.util.ArrayList;

import uniandes.unacloud.common.enums.ExecutionStateEnum;


/**
 * Entity to represent a image that has been deployed on infrastructure,
 * this entity has a relationship with deployment and executions that has been requested to be executed.
 * @author CesarF
 *
 */
class DeployedImage {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * representation of the image
	 */
	Image image
	
	/**
	 * Represents if image is configured to deploy in high availability machines
	 */
	boolean highAvaliavility
	
	/**
	 * list of deployed nodes from the image
	 */
	static hasMany = [executions: Execution]
	
	/**
	 * Representation of deployed cluster 
	 */
    static belongsTo = [deployment: Deployment]
	
	/**
	 * When image is deleted, deployment history don't be. Therefore we allow image as nullable.
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
	def getActiveExecutions() {
		return executions.findAll{it.status != ExecutionStateEnum.FINISHED}.sort{it.id}
	}
	
	/**
	 * Returns the current hardware profile configured in executions
	 * @return hardware profile from first execution
	 */
	def getDeployedHarwdProfile() {
		return executions.first().getHardwareProfile()
	}
	
	/**
	 * Returns the hostname base for all deployments
	 * @return String with the host name
	 */
	def getDeployedHostname() {
		def ip = executions.first().mainIp().ip.split('\\.')
		return executions.first().getName().substring(0, executions.first().getName().length() - (ip[2].length() + ip[3].length()))
	}
	
	/**
	 * Returns database id for this entity
	 * @return Long id
	 */
	def Long getDatabaseId() {
		return id;
	}
	
	/**
	 * Returns image
	 * @return image
	 */
	def Image getImage() {
		return image
	}
}
