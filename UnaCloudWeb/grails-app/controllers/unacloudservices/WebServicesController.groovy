package unacloudservices

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.transaction.annotation.Transactional;

import unacloud2.WebServicesService;

class WebServicesController{
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	
	static responseFormats = ['json', 'xml']
	
	/**
	 * Representation of web service logic
	 */
	
	WebServicesService webServicesService
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Starts a cluster and renders a response
	 * @param login user login
	 * @param apiKey user API key
	 * @param cluster cluster data to be deployed
	 * @return service logic response as JSON
	 */
	
	def startCluster(String login,String apiKey,String cluster){
		JSONObject jsonCluster= new JSONObject(cluster)
		render webServicesService.startCluster(login,apiKey,jsonCluster) as JSON
	}
	
	def uploadFile(String login, String apiKey, String fileName){
		InputStream file = params.file
		render webServicesService.uploadFile(login, apiKey,file) as JSON
	}
	
	def deleteFile(String login, String apiKey, String fileName){
		render webServicesService.deleteFile(login, apiKey, fileName) as JSON
	}
	
	def listFiles(String login, String apiKey){
		render webServicesService.listFiles(login,apiKey) as JSON
	}
	
	def externalDeploy(String login, String apiKey, String cluster){
		JSONObject jsonCluster= new JSONObject(cluster)
		render webServicesService.externalDeploy(login,apiKey,jsonCluster) as JSON
	}
	
	/**
	 * Starts an heterogeneus cluster and renders a response
	 * @param login user login
	 * @param apiKey user API key
	 * @param cluster cluster data to be deployed
	 * @return service logic response as JSON
	 */
	
	def startHeterogeneousCluster(String login,String apiKey, String cluster){
		JSONObject jsonCluster= new JSONObject(cluster)
		render webServicesService.startHeterogeneousCluster(login,apiKey,jsonCluster) as JSON
	}
	
	/**
	 * Gives the list of clusters available for this user
	 * @param login user login
	 * @param apiKey user API key
	 * @return service logic response as JSON
	 */
	
	def getClusterList(String login,String apiKey){
		render webServicesService.getClusterList(login,apiKey) as JSON	
	}
	
	/**
	 * Stops a virtual machine
	 * @param login user login
	 * @param apiKey user API key
	 * @param machineId Id of the virtual machine to be stopped  
	 * @return service logic response as JSON
	 */
	def stopVirtualMachine(String login, String apiKey, String machineId){
		render webServicesService.stopVirtualMachine(login,apiKey,machineId) 
	}
	
	/**
	 * Stops all virtual machines belonging to a deployment
	 * @param login
	 * @param apiKey
	 * @param depId
	 * @return service logic response as JSON
	 */
	def stopDeployment(String login, String apiKey, String depId){
		render webServicesService.stopDeployment(login,apiKey,depId)
	}
	
	/**
	 * Not implemented yet
	 */
	def restartVirtualMachines(){
			
	}
	
	/**
	 * Return user's active deployments
	 * @param login user login
	 * @param apiKey user API key
	 * @return service logic response as JSON
	 */
	def getActiveDeployments(String login,String apiKey){
		render webServicesService.getActiveDeployments(login,apiKey) as JSON
	}
	
	/**
	 * Returns all deployment information
	 * @param login user login
	 * @param apiKey user API key
	 * @param depId Deployment id
	 * @return
	 */
	def getDeploymentInfo(String login,String apiKey, String depId){
		render webServicesService.getDeploymentInfo(login,apiKey,depId) as JSON
	}
	
	/**
	 * Changes the policy of virtual machine assignment  
	 * @param login user login
	 * @param apiKey user API key
	 * @param allocationPolicy new allocation method to be used
	 * @return service logic response as JSON
	 */
	
	def changeAllocationPolicy(String login,String apiKey, String allocationPolicy){
		render webServicesService.changeAllocationPolicy(login,apiKey,allocationPolicy)		
	}
	
	/**
	 * Add new instances to a deployed image
	 * @param login user login
	 * @param apiKey user API key
	 * @param imageId Id of the image to be deployed
	 * @param instances number of instances to be deployed
	 * @param time execution time of the new instances in minutes
	 * @return service logic response as JSON
	 */
	def addInstances(String login,String apiKey, String imageId,int instances,int time){		
		render webServicesService.addInstances(login,apiKey,imageId,instances,time) as JSON
	}
}
