package unacloud

import unacloud.enums.VirtualMachineExecutionStateEnum;
import webutils.ImageRequestOptions;
import grails.transaction.Transactional

@Transactional
class DeploymentService {	
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of Lab service
	 */
	LaboratoryService laboratoryService
	
	/**
	 * Representation of User Restriction service
	 */
	UserRestrictionService userRestrictionService
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    /**
	 * Deploys a new cluster which virtual machines could be
	 * heterogeneous for the same image  
	 * @param cluster cluster to be deployed
	 * @param user owner of the deployment
	 * @param time execution time in millisecond
	 * @param options group of deployment properties for each image
	 * @throws Exception if any of the deployment processes fails
	 * @return deploy created deployment entity
	 */
	
	def synchronized deploy(Cluster cluster, User user, long time, ImageRequestOptions[] requests) throws Exception{
		
		//Validates that hardware profile is available for user and there are enough host to deploy
		def hwdProfilesAvoided = userRestrictionService.getAvoidHwdProfiles(user)
		requests.each{			
			if(!(it.hp in hwdProfilesAvoided)) throw new Exception('Hardware profile does not exist or You don\'t have permissions to use selected one')	
		}
				
		def labsAvoided = userRestrictionService.getAvoidLabs(user)
		if(labsAvoided.size()==0) throw new Exception('No physical machines available')
		
		println "Deploying"
		DeployedCluster depCluster= new DeployedCluster(cluster: cluster)
		depCluster.images=[]		
		
		requests.eachWithIndex(){ request,i->
			def depImage= new DeployedImage(image:request.image,highAvaliavility:request.high)
			depImage.save(failOnError: true)
			for(int j=0;j<request.instances;j++){				
				def virtualMachine = new VirtualMachineExecution(name: request.hostname,message: "Initializing",  hardwareProfile: request.hp,disk:0,status: VirtualMachineExecutionStateEnum.REQUESTED,deployImage:depImage, interfaces:[])
				virtualMachine.save(failOnError: true)				
			}
						
			ArrayList<PhysicalMachine> pms = new ArrayList<>()
			labsAvoided.each{
				pms.addAll(it.getAvailableMachines(request.high))
			}
			
			depCluster.images.add(depImage)
		}
		depCluster.save(failOnError: true)
		
		
//		/*
//		 * Makes allocation and user restriction validations for each image
//		 */
//		for (image in depCluster.images)
//		deploymentProcessorService.doDeployment(image,user,false)
//		/*
//		 * Creates deployment entity and links it to the user
//		 */
//		long stopTimeMillis= new Date().getTime()
//		def stopTime= new Date(stopTimeMillis +time)
//		
//		Deployment dep
//		if(!Environment.isDevelopmentMode()){
//			dep = new Deployment(cluster: depCluster, startTime: new Date(),stopTime: stopTime,status: DeploymentStateEnum.ACTIVE)
//		
//			dep.save(failOnError: true)
//			if(user.deployments==null)
//				user.deployments=[]
//			user.deployments.add(dep)
//			user.save(failOnError: true)
//			/*
//			 * Finally it sends the deployment message to the agents
//			 */
//			runAsync{ deployerService.deploy(dep) }
//		}		
//		
//		return dep
	}
}
