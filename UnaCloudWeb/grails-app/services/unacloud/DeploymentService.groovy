package unacloud

import unacloud.allocation.IPAllocatorService
import unacloud.allocation.PhysicalMachineAllocatorService
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.task.queue.QueueTaskerControl;
import webutils.ImageRequestOptions;
import grails.transaction.Transactional
import grails.util.Environment;

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
	
	/**
	 * Representation of the physical machine allocator service
	 */
	
	PhysicalMachineAllocatorService physicalMachineAllocatorService
	
	/**
	 * Representation of the IP allocator service
	 */
	
	IPAllocatorService ipAllocatorService
	
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
		if(labsAvoided.size()==0) throw new Exception('Not enough physical machines available')
		
		def ipsAvailable = []
		
				
		println "Deploying"
		DeployedCluster depCluster= new DeployedCluster(cluster: cluster)
		depCluster.images=[]		
		
		requests.eachWithIndex(){ request,i->
			def depImage= new DeployedImage(image:request.image,highAvaliavility:request.high)
			depImage.save(failOnError: true)
			for(int j=0;j<request.instances;j++){				
				def virtualMachine = new VirtualMachineExecution(name: request.hostname,message: "Initializing",  hardwareProfile: request.hp,disk:0,status: VirtualMachineExecutionStateEnum.REQUESTED,deployImage:depImage,startTime: new Date(), interfaces:[])
				virtualMachine.save(failOnError: true)				
			}
						
			List<PhysicalMachine> pms = new ArrayList<>()
			labsAvoided.each{
				pms.addAll(it.getAvailableMachines(request.high))
			}
			if(pms.size()==0) throw new Exception('Not enough physical machines available')
			
			physicalMachineAllocatorService.allocatePhysicalMachines(user,depImage.virtualMachines,pms)
			ipAllocatorService.allocateIPAddresses(depImage)			
			depCluster.images.add(depImage)
		}
		depCluster.save(failOnError: true)
	
		Deployment dep
		if(!Environment.isDevelopmentMode()){
			dep = new Deployment(user:user,cluster: depCluster, startTime: new Date(),status: DeploymentStateEnum.ACTIVE)		
			dep.save(failOnError: true)
			
			QueueTaskerControl.deployCluster(dep,user)
		}		
		
		return dep
	}
}
