package unacloud2

import org.apache.commons.io.FileUtils;
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject

import back.pmallocators.AllocatorEnum;
import back.services.ExternalCloudCallerService

import com.amazonaws.services.ec2.model.RunInstancesResult

import unacloud2.enums.DeploymentStateEnum;
import webutils.ImageRequestOptions;
import wsEntities.WebServiceException


class WebServicesService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of deployment services
	 */
	
	DeploymentService deploymentService
	ExternalCloudCallerService externalCloudCallerservice
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Builds a new cluster and deploys it
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @param cluster JSON Object with the deployment option data
	 * @return deployment object created
	 * @throws Exception if the user credentials doesn't match or the deployment fails
	 */
	
	def startCluster(String login,String apiKey,JSONObject cluster) throws Exception{
		println login+":"+apiKey+":"+cluster
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		JSONArray images= cluster.getJSONArray("images")
		ImageRequestOptions[] options= new ImageRequestOptions[images.length()]
		boolean[] highAvailability = new boolean[images.length()]
		for(int i=0; i<images.length();i++){
			JSONObject image=images.get(i)
			options[i]= new ImageRequestOptions(image.get("imageId"), HardwareProfile.findByName(image.get("hardwareProfile")), image.getInt("instances"), image.getString("hostname"))
			highAvailability[i]=false
		}
		def userCluster= Cluster.get(cluster.get("clusterId"))
		return deploymentService.deploy(userCluster, user, (Long)cluster.getInt("execTime")*60000,options,highAvailability)
	}
	
	/**
	 * Stops a deployment execution
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @param depId id of deployment to be stopped
	 * @return deployment with the stop changes
	 */
	
	def stopDeployment(String login,String apiKey,String depId){
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		Deployment deployment= Deployment.get(depId)
		def deps=user.deployments
		def belongsToUser= false
		for (dep in deps){
			if(dep.equals(deployment)){
				belongsToUser=true
			}
		}
		if(!belongsToUser)return new WebServiceException("Cannot stop that deployment because it doesn´t belong to user")
		for(image in deployment.cluster.images){
			for(vm in image.virtualMachines){
				deploymentService.stopVirtualMachineExecution(vm)
			}
		}
		deploymentService.stopDeployments(user)
		return deployment
	}
	
	/**
	 * Stops a single virtual machine
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @param machineId virtual machine's id
	 * @return virtual machine with the stop changes
	 * @throws Exception if stop process fails or credentials doesn't match
	 */
	
	def stopVirtualMachine(String login,String apiKey,String machineId) throws Exception{
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		VirtualMachineExecution vme= VirtualMachineExecution.get(machineId)
		def deps=user.deployments
		def belongsToUser= false
		for (dep in deps){
			if(dep.isActive()){
				for(image in dep.cluster.images){
					for (vm in image.virtualMachines){
						if(vm.equals(vme)){
							belongsToUser=true
							break
						}
					}
				}
			}
		}
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Cannot stop that machine because it doesn´t belong to user")
		def resp =deploymentService.stopVirtualMachineExecution(vme)	
		deploymentService.stopDeployments(user)
		return resp
	}
	
	/**
	 * Starts a cluster with different VM configurations per image   
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @param cluster heterogeneous cluster deployment options
	 * @return deployment object created
	 * @throws Exception if the user credentials doesn't match or the deployment fails
	 */
	
	def startHeterogeneousCluster(String login,String apiKey,JSONObject cluster) throws Exception{
		try {
			println 'StartDeployment Heterogeneous'
			if(login==null||apiKey==null)return new WebServiceException("invalid request")
			User user= User.findByUsername(login)
			if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
			if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
			JSONArray images= cluster.getJSONArray("images")
			ImageRequestOptions[] options= new ImageRequestOptions[images.length()]
			for(int i=0; i<images.length();i++){
				JSONObject image=images.get(i)
				options[i]= new ImageRequestOptions(image.getLong("imageId"), HardwareProfile.findByName(image.get("hardwareProfile")), image.getInt("instances"),image.getString("hostname"))
			}
			def userCluster= Cluster.get(cluster.get("clusterId"))
			if (userCluster.isDeployed()) return new WebServiceException("Cluster already deployed")
			else return deploymentService.deployHeterogeneous(userCluster, user, cluster.getInt("execTime")*60000,options)
			
		} catch (Exception e) {
			e.printStackTrace()
			throw e
		}
		
	}
	
	def uploadFile(String login, String apiKey, InputStream file, String fileName){
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		File f= new File(fileName)
		FileUtils.copyInputStreamToFile(f, file)
		externalCloudCallerservice.uploadFile(f, user)
		f.delete()
		return "File uploaded"
	}
	
	def getFileDownloadLink(String login, String apiKey, String objectName){
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		externalCloudCallerservice.listUserObjects(user)
	}
	
	def deleteFile(String login, String apiKey, String fileName){
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		return externalCloudCallerservice.deleteFile(user, fileName)
	}
	
	def listFiles(String login, String apiKey){
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		return externalCloudCallerservice.listUserObjects(user)
	}
	
	def externalDeploy(String login,String apiKey,JSONObject cluster){
		
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login)
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		JSONArray images= cluster.getJSONArray("images")
		ImageRequestOptions[] options= new ImageRequestOptions[images.length()]
		for(int i=0; i<images.length();i++){
			JSONObject im=images.get(i)
			VirtualMachineImage image = VirtualMachineImage.get(im.getLong('imageId'))
			if (image!= null) {
				if(image.externalId==null) return new WebServiceException( "Some images had not been uploaded to the external cloud account and cannot be deployed")
							
				RunInstancesResult rir= externalCloudCallerservice.runInstances(image.externalId, Integer.parseInt(params.instances), HardwareProfile.get(params.hardwareProfile).name, user)
				return deploymentService.externalDeploy(cluster,user,rir)
			}
			else return new WebServiceException( "Image id '"+image.id+"' not found" )
		}				
	}
	
	/**
	 * 
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @return cluster list with its properties
	 * @throws Exception if the user credentials didn't match
	 */
	
	
	def getClusterList(String login,String apiKey) throws Exception{
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login);
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		def clusterList= new JSONArray()
		
		for(cluster in user.userClusters){
			def clusterProperties= new JSONObject()
			clusterProperties.put("cluster_id",cluster.id)
			clusterProperties.put("name",cluster.name)	
			def images= new JSONArray()
			for(image in cluster.images){
				JSONObject imageProperties= new JSONObject()
				imageProperties.put("image_id", image.id)
				imageProperties.put("name", image.name)
				images.put(imageProperties)
			}
			clusterProperties.put("images",images)
			clusterList.put(clusterProperties)
		}
		return clusterList
	}
	
	/**
	 * Returns a list with the active deployments for the user
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @return active deployments list
	 * @throws Exception if the user credentials didn't match
	 */
	
	def getActiveDeployments(String login,String apiKey) throws Exception{
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login);
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		def deps= new ArrayList()
		for(dep in user.deployments){
			if (dep.status.equals(DeploymentStateEnum.ACTIVE))
			deps.add(dep)
		}
		if (deps.isEmpty())return new WebServiceException("There's no active deployments for this user")
		JSONArray resp= new JSONArray()
		for(dep in deps){
			JSONObject depProps= new JSONObject()
			depProps.put("deployment_id", dep.id)
			depProps.put("cluster_name", dep.cluster.cluster.name)
			depProps.put("cluster_id", dep.cluster.cluster.id)
			resp.put(depProps)
		}
		return resp
	}
	
	/**
	 * Returns deployment detailed info per VM  
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @param depId
	 * @return list of all VM details in the deployment
	 * @throws Exception if the user credentials didn't match
	 */
	
	def getDeploymentInfo(String login,String apiKey,String depId) throws Exception{
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login);
		if(user==null||user.apiKey==null)return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey))return new WebServiceException("Invalid Key")
		
		
		def vms= new JSONArray()
		def dep= Deployment.get(depId)
		if(!dep.isActive())return new WebServiceException("This deployment is not active")
		for (image in dep.cluster.images){
			for(vm in image.virtualMachines){
				def data= new JSONObject()
				data.put("belongs_to_image",image.image.id)
				data.put("status",vm.status.toString())
				data.put("stop_time", vm.stopTime.getTime())
				data.put("ip",vm.ip.ip)
				data.put("message",vm.message)
				data.put("hostname",vm.name)
				data.put("id", vm.id)
				vms.put( data)
			}
		}
		return vms
	}
	
	/**
	 * Changes the VM/PM allocation algorithm
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @param allocationPolicy new allocation policy to be used
	 * @return Success response
	 * @throws Exception if the user credentials didn't match
	 */
	
	def changeAllocationPolicy(String login,String apiKey,String allocationPolicy) throws Exception{
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login);
		if(user.userType.equals("User")) return new WebServiceException("You're not administrator")
		if(user==null||user.apiKey==null) return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey)) return new WebServiceException("Invalid Key")
		def alloc
		try{
			alloc=AllocatorEnum.valueOf(allocationPolicy)
		}catch(Exception e){
			return e
		}
		
		if(alloc==null) throw new WebServiceException("Allocator not found")
		def variable=ServerVariable.findByName("VM_ALLOCATOR_NAME")
		variable.putAt("variable", alloc.toString())
		return "Success"
		
	}
	
	/**
	 * Adds new instances to the selected image
	 * @param login request owner username
	 * @param apiKey request owner API key
	 * @param imageId image id to which instances will be increased
	 * @param instances number of new instances
	 * @param time new instances execution time 
	 * @return deployed image with the new instances
	 * @throws Exception if the user credentials didn't match or the deployment fails
	 */
	
	def addInstances(String login,String apiKey,String imageId,int instances,long time) throws Exception{
		if(login==null||apiKey==null)return new WebServiceException("invalid request")
		User user= User.findByUsername(login);
		if(user.userType.equals("User")) return new WebServiceException("You're not administrator")
		if(user==null||user.apiKey==null) return new WebServiceException("Invalid User")
		if(!apiKey.equals(user.apiKey)) return new WebServiceException("Invalid Key")
		DeployedImage image= DeployedImage.get(imageId)
		if(image==null)return new WebServiceException("Image not found")
		boolean belongsToUser=false
		for(dep in user.deployments){
			for(i in dep.cluster.images){
				if(image.equals(i)){
					belongsToUser=true
					break
				}
			}
		}
		if(!belongsToUser)return new WebServiceException("This image wasn't deployed by the given user")	
		return deploymentService.addInstances(image.id, instances, time.toLong()*60*1000)
	}
}
