package unacloud

import unacloud.enums.ClusterEnum;
import unacloud.share.enums.DeploymentStateEnum;
import unacloud.share.enums.UserStateEnum;
import unacloud.share.enums.VirtualMachineExecutionStateEnum;
import unacloud.share.enums.VirtualMachineImageEnum;
import webutils.ImageRequestOptions;

class DeploymentController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of cluster service
	 */
	DeploymentService deploymentService
	
	/**
	 * Representation of Lab service
	 */
	LaboratoryService laboratoryService
	
	/**
	 * Representation of User Restriction service
	 */
	UserRestrictionService userRestrictionService
	
	//-----------------------------------------------------------------
	// Actions MVC
	//-----------------------------------------------------------------

	/**
	 * Makes session verifications before executing any action
	 */
	
	def beforeInterceptor = {
		if(!session.user){
			flash.message="You must log in first"
			redirect(uri:"/login", absolute:true)
			return false
		}		
		def user = User.get(session.user.id)
		session.user.refresh(user)
		if(!user.status.equals(UserStateEnum.AVAILABLE)){
			flash.message="You don\'t have permissions to do this action"
			redirect(uri:"/", absolute:true)
			return false
		}
	}
	
	
	/**
	 * Deploy action. Checks image number and depending on it, forms the parameters in
	 * order to pass them to the service layer. It also catches exceptions and pass
	 * them to error view. If everything works it redirects to list deployment view.
	 */
	
	def deploy(){
		Cluster cluster= Cluster.get(params.id)
		if(cluster){			
			def user= User.get(session.user.id)	
			//validates if user is owner to deploy cluster
			if(user.userClusters.find {it.id==cluster.id}!=null && cluster.state.equals(ClusterEnum.AVAILABLE)){
				//Validates if images are available in the platform
				def unavailable = cluster.images.findAll{it.state==VirtualMachineImageEnum.AVAILABLE}
				if(unavailable.size()!=cluster.images.size()){
					flash.message= "Some images of this cluster are not available at this moment. Please, change cluster to deploy or images in cluster."
					redirect(uri:"/services/cluster/deploy/"+cluster.id, absolute:true)	
					return
				}
				try {
					//validates if cluster is good configured
					def requests=new ImageRequestOptions[cluster.images.size()];
					cluster.images.eachWithIndex {it,idx->
						HardwareProfile hp= HardwareProfile.get(params.get('option_hw_'+it.id))
						requests[idx]=new ImageRequestOptions(it, hp,params.get('instances_'+it.id).toInteger(), params.get('host_'+it.id),(params.get('highAvailability_'+it.id))!=null);
					}		
					deploymentService.deploy(cluster, user, params.time.toLong()*60*60*1000, requests)
					redirect(uri:"/services/deployment/list", absolute:true)
					return
					
				} catch (Exception e) {
					e.printStackTrace()
					if(e.message==null)
						flash.message= e.getCause()
					else
						flash.message=e.message
					redirect(uri:"/services/cluster/deploy/"+cluster.id, absolute:true)
					return
				}
			}else{
				flash.message='You don\'t have permissions to deploy this cluster or cluster is not available'
				redirect(uri:"/services/cluster/deploy/"+cluster.id, absolute:true)
				return
			}	
		}
		redirect(uri:"/services/cluster/list", absolute:true)		
	}
	
	
	/**
	 * Deployment list action. Controls view all function 
	 * @return deployments that must be shown according to view all checkbox
	 */
	
	def list(){
		def user = User.get(session.user.id)
		if(!user.isAdmin()){
			[myDeployments: user.getActiveDeployments()]
		}
		else {	
			def deployments = deploymentService.getActiveDeployments(user)	
			[myDeployments: user.getActiveDeployments(),deployments: deployments]
		}
	}
	
	
	/**
	 * Stop execution action. All nodes selected on the deployment interface with status FAILED or DEPLOYED will be
	 * stopped. Redirects to index when the operation is finished.
	 */
	
	def stop(){
		def user= User.get(session.user.id)
		List<VirtualMachineExecution> executions = new ArrayList<>();
		params.each {
			if (it.key.contains("execution_")){
				if (it.value.contains("on")){
					VirtualMachineExecution vm = VirtualMachineExecution.get((it.key - "execution_") as Integer)
					if(vm != null && (vm.status.equals(VirtualMachineExecutionStateEnum.DEPLOYED)||vm.status.equals(VirtualMachineExecutionStateEnum.FAILED))){
						if(vm.deployImage.deployment.user == user || user.isAdmin())
							executions.add(vm)
					}
				}
			}
		}	
		if(executions.size()>0){
			flash.message='Your request has been processed'
			flash.type='info'
			deploymentService.stopVirtualMachineExecutions(executions,user)
		}else flash.message='Only executions with state FAILED or DEPLOYED can be selected to be FINISHED'
		redirect(uri:"/services/deployment/list", absolute:true)
	}
	
	/**
	 * Render form to add instances to a current deployed image
	 * @param deployed image
	 * @return render form
	 */
	def addInstances(){
		DeployedImage image = DeployedImage.get(params.id);
		if(image && image.deployment.status.equals(DeploymentStateEnum.ACTIVE)){
			def user= User.get(session.user.id)
			if(image.deployment.user==user||user.isAdmin()){
				
				def hwdProfilesAvoided = []
				hwdProfilesAvoided.add(image.getDeployedHarwdProfile())
				def labsAvoided = userRestrictionService.getAvoidLabs(image.deployment.user)
				def quantitiesTree = new TreeMap<String, Integer>()
				labsAvoided.each {
					def results = laboratoryService.calculateDeploys(it,hwdProfilesAvoided, image.highAvaliavility)					
					for(HardwareProfile hwd in hwdProfilesAvoided){
						if(!quantitiesTree.get(hwd.name))quantitiesTree.put(hwd.name,results.get(hwd.name))
						else quantitiesTree.put(hwd.name,results.get(hwd.name)+quantitiesTree.get(hwd.name))
					}
				}
				def quantities = []
				def high = false;
				for(HardwareProfile hwd in hwdProfilesAvoided){
					quantities.add(['name':hwd.name,'quantity':quantitiesTree.get(hwd.name)])
				}
				[quantities:quantities,image:image]
			}else{
				flash.message='You do not have privileges to edit this deployed image'
				redirect(uri:"/services/deployment/list", absolute:true)
			}
		}else{
			redirect(uri:"/services/deployment/list", absolute:true)
		}
	}
	
	/**
	 * Add new instances to a selected deployed image
	 * @return
	 */
	def saveInstances(){
		DeployedImage image = DeployedImage.get(params.id);
		if(image && image.deployment.status.equals(DeploymentStateEnum.ACTIVE)){
			def user= User.get(session.user.id)
			if(image.deployment.user==user||user.isAdmin()){
				try {
					//validates if cluster is good configured
					def request=new ImageRequestOptions(image.image, image.getDeployedHarwdProfile(),params.get('instances_'+image.id).toInteger(), image.getDeployedHostname(),image.highAvaliavility);
				
					deploymentService.addInstances(image,user, params.time.toLong()*60*60*1000,request)
					redirect(uri:"/services/deployment/list", absolute:true)
					return
					
				} catch (Exception e) {
					e.printStackTrace()
					if(e.message==null)
						flash.message= e.getCause()
					else
						flash.message=e.message
					redirect(uri:"/services/deployment/"+image.id+'/add', absolute:true)
				}
				
			}else{
				flash.message='You do not have privileges to edit this deployed image'
				redirect(uri:"/services/deployment/list", absolute:true)
			}
		}else{
			redirect(uri:"/services/deployment/list", absolute:true)
		}
	}
	
	/**
	 * Validates if user has permissions and call to deploymentService to create a new task to create a copy
	 * @return
	 */
	def createCopy(){
		VirtualMachineExecution execution = VirtualMachineExecution.get(params.id)
		if(execution){
			def user= User.get(session.user.id)
			if(execution.deployImage.deployment.user==user||user.isAdmin()){
				try{					
					deploymentService.createCopy(execution, execution.deployImage.deployment.user, params.name)
					flash.message='Your request has been sent'
					flash.type='info'
				}catch(Exception e){
					if(e.message==null)
						flash.message= e.getCause()
					else
						flash.message=e.message
				}
			}else{
				flash.message='You do not have privileges to create a copy from this execution'
				redirect(uri:"/services/deployment/list", absolute:true)
			}
		}
		redirect(uri:"/services/deployment/list", absolute:true)
	}
}
