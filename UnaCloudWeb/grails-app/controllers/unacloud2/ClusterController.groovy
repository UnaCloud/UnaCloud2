package unacloud2

import back.userRestrictions.UserRestrictionEnum;
import back.userRestrictions.UserRestrictionProcessorService;
import webutils.ImageRequestOptions;
import grails.converters.JSON

class ClusterController {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of cluster services
	 */
	
	ClusterService clusterService
	
	/**
	 * Representation of UserRestriction services
	 */
	UserRestrictionProcessorService userRestrictionProcessorService
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
	}
	
	/**
	 * Index action that sends cluster lists
	 * @return ordered clusters of the session user
	 */
    def index() { 
		[clusters: session.user.getOrderedClusters()]
	}
	
	/**
	 * New cluster creation action that sends the available images for the user
	 * @return list of ordered images that user can add to a new cluster
	 */
	def newCluster(){
		ArrayList<VirtualMachineImage> images = session.user.getOrderedImages()
		VirtualMachineImage.all.each {
			if(it.isPublic&&!(images.contains(it))){
				images.add(it)
			}
		}
		[images: images]
	}
	
	/**
	 * Save action of a new cluster. It redirects to index after saving  
	 */
	def save(){
		
		def cluster = new Cluster( name: params.name)
		def user = User.get(session.user.id)
		clusterService.saveCluster(params.images, cluster, user)
		redirect(action: 'index')
	}
	
	/**
	 * Deploy options action that brings the form with deploying options for each 
	 * image
	 * @return limits shown in the information of form and cluster to be deployed
	 */
	def deployOptions(){
		def cluster=Cluster.get(params.id);
		int limit
		int limitHA
		def user = User.get(session.user.id)		
		def machines = userRestrictionProcessorService.getAvoidedMachines(user)
		limitHA = machines.findAll{it.highAvailability==true}.size()
		limit = machines.size() - limitHA;
		int maxDeploys = clusterService.calculateMaxDeployments(user, HardwareProfile.findByName("small"))
		[cluster: cluster,limit: limit, limitHA: limitHA, hardwareProfiles: HardwareProfile.list(), max:maxDeploys]		
	}
	
	def externalDeployOptions(){
		def cluster=Cluster.get(params.id);
		for (image in cluster.images){
			if(image.externalId==null){
				flash.message= "One or more images in this cluster are not uploaded on the external provider"
				redirect( uri: "/error",absolute: true )
			}
		}
		ServerVariable computingAccount= ServerVariable.findByName('EXTERNAL_COMPUTING_ACCOUNT')
		ExternalCloudAccount account
		if(computingAccount!=null &&  !(computingAccount.variable.equals('None'))){
			account = ExternalCloudAccount.findByName(computingAccount.variable)
			[account:account,cluster: cluster, hardwareProfiles: HardwareProfile.findByProvider(account.provider)]
		
		}
		else{
			flash.message= "There isn't any configured account for external deployments"
			redirect( uri: "/error",absolute: true )
		}
		
		
	}
	/**
	 * Delete cluster action. Receives the cluster id in the params map and 
	 * responses success and redirect to index after deletion  
	 * @return
	 */
	def delete(){
		def resp;
		def cluster = Cluster.get(params.id)
		if (!cluster) {
			resp = [success:false];
			//redirect(action:"index")
		}
		else if (cluster.isDeployed()) {
			resp = [success:false, message:'The cluster is currently deployed'];
			//redirect(action:"index")
		}
		else{
			def user= User.get(session.user.id)
			clusterService.deleteCluster(cluster, user)
			resp = [success:true,'redirect':'index'];
			//redirect(action:"index")
		}
		render resp as JSON;
	}
	/**
	 * TODO Documentation and manage High Avaliability
	 * @return
	 */
	def maxDeploys(){
		def resp;
		try{
			def user = User.get(session.user.id)
			def hwp = HardwareProfile.get(Long.parseLong(params.hwp))
			resp = [max:clusterService.calculateMaxDeployments(user, hwp)];
		}catch(Exception e){
			e.printStackTrace()
			resp = [max:-1];
		}		
		render resp as JSON;
	}
		
}
