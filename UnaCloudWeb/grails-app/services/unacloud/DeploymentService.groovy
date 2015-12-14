package unacloud

import unacloud.allocation.IpAllocatorService
import unacloud.allocation.PhysicalMachineAllocatorService
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.pmallocators.AllocatorException
import unacloud.pmallocators.PhysicalMachineAllocationDescription
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
	
	IpAllocatorService ipAllocatorService
	
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
	
	def synchronized deploy(Cluster cluster, User user, long time, ImageRequestOptions[] requests) throws Exception, AllocatorException{
		
		//Validates that hardware profile is available for user and there are enough host to deploy
		def hwdProfilesAvoided = userRestrictionService.getAvoidHwdProfiles(user)
		requests.each{			
			if(!(it.hp in hwdProfilesAvoided)) throw new Exception('Hardware profile does not exist or You don\'t have permissions to use selected one')	
		}
				
		def labsAvoided = userRestrictionService.getAvoidLabs(user)
		if(labsAvoided.size()==0) throw new Exception('Not enough physical machines available')
		
		List<PhysicalMachine> pms = new ArrayList<>()
		List<PhysicalMachine> pmsHigh = new ArrayList<>()
		
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptions = physicalMachineAllocatorService.getPhysicalMachineUsage(pms)
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptionHigh = physicalMachineAllocatorService.getPhysicalMachineUsage(pmsHigh)
		
		labsAvoided.each{
			pms.addAll(it.getAvailableMachines(false))
			pmsHigh.addAll(it.getAvailableMachines(true))
		}
		
		def images = []
		requests.eachWithIndex(){ request,i->
			
			def depImage= new DeployedImage(image:request.image,highAvaliavility:request.high,virtualMachines:[])			
			def executions = []
			for(int j=0;j<request.instances;j++){				
				def virtualExecution = new VirtualMachineExecution(deployImage: depImage,name: request.hostname,message: "Initializing",  hardwareProfile: request.hp,disk:0,status: VirtualMachineExecutionStateEnum.REQUESTED,startTime: new Date(), interfaces:[])
				executions.add(virtualExecution)
			}
			depImage.virtualMachines=executions
			images.add(depImage)	
				
			println 'Load Map with used machines '+pmDescriptions.entrySet().size()
			for (Map.Entry<Long,PhysicalMachineAllocationDescription> entry : pmDescriptions.entrySet()) {
				println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
			}
			
			if(!depImage.highAvaliavility&&pms.size()==0) throw new Exception('Not enough physical machines available')
			if(depImage.highAvaliavility&&pmsHigh.size()==0) throw new Exception('Not enough high availability physical machines available')
			
			physicalMachineAllocatorService.allocatePhysicalMachines(user,depImage.virtualMachines.sort(),depImage.highAvaliavility?pmsHigh:pms,depImage.highAvaliavility?pmDescriptionHigh:pmDescriptions)
			ipAllocatorService.allocateIPAddresses(depImage.virtualMachines)
			
		}	
		Date start = new Date()
		Date stop = new Date(start.getTime()+time)
		Deployment dep = new Deployment(user:user,startTime: start, stopTime: stop,status: DeploymentStateEnum.ACTIVE, cluster:cluster)
		dep.save(failOnError: true)		
				
		for(DeployedImage image in images){
			image.deployment = dep
			image.save(failOnError: true)
			for(VirtualMachineExecution execution in image.virtualMachines){
				execution.deployImage = image
				execution.saveExecution()
			}
		}
		
		if(!Environment.isDevelopmentMode()){	
			
			QueueTaskerControl.deployCluster(dep,user)
		}		
		
		return dep
	}
	
	/**
	 * Return the list of active deployments that owner is different from parameter user
	 * @param user owner to filter list
	 * @return list of deployments
	 */
	def getActiveDeployments(User user){
		List deployments= new ArrayList()
		def deps = Deployment.where{status==DeploymentStateEnum.ACTIVE && user != user}.findAll()
		for (Deployment dep in deps){
			if(dep.isActive())
				deployments.add(dep)
		}
		return deployments
	}
	/**
	 * 
	 * @param executions
	 * @return
	 */
	def stopVirtualMachineExecutions(List<VirtualMachineExecution> executions){
		
	}
}
