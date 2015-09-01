
package unacloud2


import back.services.ExternalCloudCallerService;
import back.services.ImageUploadService;
import back.userRestrictions.UserRestrictionProcessorService
import grails.converters.JSON

import java.util.regex.Pattern.Start;

import com.amazonaws.services.ec2.model.RunInstancesResult;

import unacloud2.enums.PhysicalMachineStateEnum;
import unacloud2.enums.VirtualMachineImageEnum;
import webutils.ImageRequestOptions;

class DeploymentController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user restriction services
	 */
	
	UserRestrictionProcessorService userRestrictionProcessorService
	
	/**
	 * Representation of deployment services
	 */
	
	DeploymentService deploymentService
	
	/**
	 * Representation of image upload services
	 */
	ImageUploadService imageUploadService
	

	ExternalCloudCallerService externalCloudCallerService
	
	//-----------------------------------------------------------------
	// Actions
	//-----------------------------------------------------------------
	
	/**
	 * Makes session verifications before executing any other action
	 */
	
	def beforeInterceptor = {
		if(!session.user){

			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}
		session.user.refresh()
		deploymentService.stopDeployments(User.get(session.user.id))
	}
	
	/**
	 * Deployment index action. Controls view all function 
	 * @return deployments that must be shown according to view all checkbox
	 */
	
	def index() {
		if(params.viewAll==null || params.viewAll=="false" ){
			[deployments: session.user.getActiveDeployments(), checkViewAll: false]
		}
		else if(params.viewAll=="true"){
			List deployments= new ArrayList()
			def deps=Deployment.findAllByStatus('ACTIVE')
			println "Deployments:"+deps
			for (Deployment dep in deps){
				println "Deployment:" +dep.id
				if(dep.isActive())
				deployments.add(dep)			
			}
			[deployments: deployments,checkViewAll: true]
		}
	}
	
	/**
	 * add instances action. it passes the image id
	 * @return id of the image in order to add instances
	 */
	def addInstancesOptions(){
		def machines=PhysicalMachine.findAllByState(PhysicalMachineStateEnum.ON)
		def limit=0, limitHA=0
		for (machine in machines)
		{
			if(machine.highAvailability)
			limitHA++
			else
			limit++
		}
		[id:params.id, limit:limit, limitHA:limitHA]
	}
	
	/**
	 * Deploy action. Checks image number and depending on it, forms the parameters in 
	 * order to pass them to the service layer. It also catches exceptions and pass 
	 * them to error view. If everything works it redirects to index view.
	 */
	
	def deploy(){
		Cluster cluster= Cluster.get(params.get('id'))

		int totalInstances
			def user= User.get(session.user.id)
			def imageNumber= cluster.images.size()
			if(imageNumber==1){
				totalInstances= params.instances.toInteger()
			}
			else{
				for (int i=0; i< params.instances.size();i++) {
					totalInstances+=params.instances.getAt(i).toInteger()
				}
			}
			//Validate if images are available in the platform
			def unavailable = cluster.images.findAll {it.state==VirtualMachineImageEnum.AVAILABLE}
			if(unavailable.size()!=cluster.images.size()){
				flash.message= "Some images of this cluster are not available at this moment. Please, change cluster to deploy or images in cluster."
				redirect( controller: "cluster",action: "deployOptions",  params: [id: cluster.id])
				return
			}
			
			def temp=new ImageRequestOptions[cluster.images.size()];
			def highAvail= new boolean[cluster.images.size()]
			if(imageNumber==1){
				HardwareProfile hp= HardwareProfile.get(params.get('hardwareProfile'))
				temp[0]=new ImageRequestOptions(cluster.images.first().id, hp,params.instances.toInteger(),params.hostname);
				highAvail[0]= (params.get('highAvailability'+cluster.images.first().id))!=null
			}
			else{

				cluster.images.eachWithIndex {it,idx->
					HardwareProfile hp= HardwareProfile.get(params.hardwareProfile.getAt(idx))
					highAvail[idx]=(params.get('highAvailability'+it.id))!=null
					temp[idx]=new ImageRequestOptions(it.id, hp,params.instances.getAt(idx).toInteger(), params.hostname.getAt(idx));

				}
			}
			try{
				deploymentService.deploy(cluster, user, params.time.toLong()*60*60*1000, temp, highAvail)
			}
			catch(Exception e){
				if(e.getMessage()==null)
				flash.message= e.getCause()
				else
				flash.message= e.getMessage()
				redirect( uri: "/error",absolute: true )
			
			}
			redirect(controller:"deployment")	
	}
	
	def externalDeploy(){
		Cluster cluster= Cluster.get(params.get('id'))
		def user= User.get(session.user.id)
		try{
			for(image in cluster.images){
				if (image.externalId==null) {
					flash.message= "Some images had not been uploaded to the external cloud account and cannot be deployed"
					redirect( uri: "/error",absolute: true )
				}
				
				RunInstancesResult rir= externalCloudCallerService.runInstances(image.externalId, Integer.parseInt(params.instances), HardwareProfile.get(params.hardwareProfile).name, user)
				println rir
				deploymentService.externalDeploy(cluster,user,rir)
			}
		}
		catch(Exception e){
			if(e.getMessage()==null)
			flash.message= e.getCause()
			else
			flash.message= e.getMessage()
			redirect( uri: "/error",absolute: true )
			return
		}
		redirect(controller:"deployment")
	}
	
	/**
	 * History action. Returns all data of every deployment
	 * @return deployments list
	 */
	
	def history(){

		[deployments: session.user.deployments]
	}
	
	/**
	 * Stop execution action. All nodes selected on the deployment interface will be
	 * stopped. Redirects to index when the operation is finished.
	 */
	
	def stop(){
		def user= User.get(session.user.id)
		params.each {
			if (it.key.contains("hostname")){
				if (it.value.contains("on")){
					VirtualMachineExecution vm = VirtualMachineExecution.get((it.key - "hostname") as Integer)
					deploymentService.stopVirtualMachineExecution(vm)
				}
			}
		}
		deploymentService.stopDeployments(user)
		redirect(action:"index")
	}
	
	/**
	 * Adds and executes new nodes to the selected deployed image. Redirects to 
	 * index when finished
	 */

	def addInstances(){
		//def depImage=DeployedImage.get(params.id)
		def instance=params.instances.toInteger()
		User user= User.get(session.user.id)
		try{
			deploymentService.addInstances(params.id.toLong(), user,instance, params.time.toLong()*60*60*1000)		   
		}
		catch (Exception e){
			println 'Error in addInstances'
			e.printStackTrace();
			if(e.getMessage()==null)
			flash.message= e.getCause()
			else
			flash.message= e.getMessage()
			
			redirect(uri:"/error", absolute:true)
			return
		}
		redirect(action: "index")
	}
	
	/**
	 * Validates if a name to create an image copy is not already in use.
	 * @params image: image id, machine: virtual machine execution id, name: name to validate
	 */
	def validate(){
		if(session.user==null){
			render('403',"Your session has expired.")			
			return
		}else{
			try {
				def result = []
				def user= User.get(session.user.id)
				DeployedImage di = DeployedImage.get(params.image);
				String nameW =  params.name;
				print nameW
				VirtualMachineImage vm = VirtualMachineImage.findByName(nameW);
				boolean rep=false;
				if(vm){
					def query = User.where{images{vm} && id == session.user.id};
					def owner = query.find();
					if(owner)rep= true;
				}					
				result = [replace: rep, imageId:params.image,machineId:params.machine,imageName:params.name]
				render result as JSON;
			} catch (Exception e) {
				e.printStackTrace()
				render("505",e.message)
				return
			}
		}
	}
	/**
	 * Send a request to a physical machine agent asking it to send an image file which will be stored on the server.
	 * @params image: image id, machine: virtual machine execution id, name: name to validate
	 */
	def save(){
		if(session.user==null){
			flash.message="Your session has expired."
			redirect(uri:"/error",absolute:true)
		}else{
			def user= User.get(session.user.id)
			try {
				long imageId = Long.parseLong(params.image)
				long virtualMachineId = Long.parseLong(params.machine)
				String imageName = params.name
				print imageName
				VirtualMachineExecution vm = VirtualMachineExecution.get(virtualMachineId)
				DeployedImage di = DeployedImage.get(imageId);
				if(User.where{id == user.id && images{di.image}}.find()){
					imageUploadService.saveImage(vm,di,virtualMachineId,imageName,user)					
				}else{
					flash.message="The image is not registered to your user."
					redirect(uri:"/error",absolute:true)
					return
				}
			} catch (Exception e) {
				e.printStackTrace()
				flash.message=e.message
				redirect(uri:"/error",absolute:true)
				return
			}
			redirect(controller:"deployment", action:"index")
		}
	}
}
