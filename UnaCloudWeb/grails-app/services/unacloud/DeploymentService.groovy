package unacloud

import unacloud.allocation.IpAllocatorService
import unacloud.allocation.PhysicalMachineAllocatorService
import unacloud.enums.DeploymentStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.enums.VirtualMachineImageEnum;
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
	
	/**
	 * Representation of the repository service
	 */
	
	RepositoryService repositoryService
	
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
		
		Date start = new Date()
		Date stop = new Date(start.getTime()+time)
		
		//Validates that hardware profile is available for user and there are enough host to deploy
		def hwdProfilesAvoided = userRestrictionService.getAvoidHwdProfiles(user)
		requests.eachWithIndex(){ request,i->	
			if(hwdProfilesAvoided.find{it.id==request.hp}!=null) throw new Exception('Hardware profile does not exist or You don\'t have permissions to use selected one')	
		}
				
		def labsAvoided = userRestrictionService.getAvoidLabs(user)
		if(labsAvoided.size()==0) throw new Exception('Not enough physical machines available')
		
		List<PhysicalMachine> pms = new ArrayList<>()
		List<PhysicalMachine> pmsHigh = new ArrayList<>()		
		
		labsAvoided.each{
			pms.addAll(it.getAvailableMachines(false))
			pmsHigh.addAll(it.getAvailableMachines(true))
		}		
		
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptions = physicalMachineAllocatorService.getPhysicalMachineUsage(pms)
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptionHigh = physicalMachineAllocatorService.getPhysicalMachineUsage(pmsHigh)
		
		def images = []
		requests.eachWithIndex(){ request,i->
			
			def depImage= new DeployedImage(image:request.image,highAvaliavility:request.high,virtualMachines:[])			
			def executions = []
			for(int j=0;j<request.instances;j++){				
				def virtualExecution = new VirtualMachineExecution(deployImage: depImage,name: request.hostname,message: "Initializing",  hardwareProfile: request.hp,disk:0,status: VirtualMachineExecutionStateEnum.QUEQUED,startTime: start,stopTime:stop, interfaces:[])
				executions.add(virtualExecution)
			}
			depImage.virtualMachines=executions
			images.add(depImage)	
				
//			println 'Load Map with used machines '+pmDescriptions.entrySet().size()
//			for (Map.Entry<Long,PhysicalMachineAllocationDescription> entry : pmDescriptions.entrySet()) {
//				println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
//			}
			
			if(!depImage.highAvaliavility&&pms.size()==0) throw new Exception('Not enough physical machines available')
			if(depImage.highAvaliavility&&pmsHigh.size()==0) throw new Exception('Not enough high availability physical machines available')
			
			physicalMachineAllocatorService.allocatePhysicalMachines(user,depImage.virtualMachines.sort(),depImage.highAvaliavility?pmsHigh:pms,depImage.highAvaliavility?pmDescriptionHigh:pmDescriptions)
			ipAllocatorService.allocateIPAddresses(depImage.virtualMachines)
			
		}	
		
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
		
		//if(!Environment.isDevelopmentMode()){				
			QueueTaskerControl.deployCluster(dep,user)
		//}		
		
		return dep
	}
	
	/**
	 * Add new instances in ImageRequestOptions array to a DeployedImage
	 * @param image
	 * @param user
	 * @param time
	 * @param requests
	 * @return
	 */
	def synchronized addInstances(DeployedImage image, User user, long time, ImageRequestOptions request){
		
		Date start = new Date()
		Date stop = new Date(start.getTime()+time)
		
		//Validates that hardware profile is available for user and there are enough host to deploy
		def hwdProfilesAvoided = userRestrictionService.getAvoidHwdProfiles(user)
		if(!(request.hp in hwdProfilesAvoided)) throw new Exception('You don\'t have permissions to use selected deployed image')		
				
		def labsAvoided = userRestrictionService.getAvoidLabs(user)
		if(labsAvoided.size()==0) throw new Exception('Not enough physical machines available')
		
		List<PhysicalMachine> pms = new ArrayList<>()
		
		labsAvoided.each{
			pms.addAll(it.getAvailableMachines(image.highAvaliavility))
		}
		if(pms.size()==0) throw new Exception('Not enough physical machines available')
		
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptions = physicalMachineAllocatorService.getPhysicalMachineUsage(pms)	
					
		def executions = []
		for(int j=0;j<request.instances;j++){
			def virtualExecution = new VirtualMachineExecution(deployImage: image,name: request.hostname,message: "Adding Instance",  hardwareProfile: request.hp,disk:0,status: VirtualMachineExecutionStateEnum.REQUESTED,startTime: new Date(), stopTime:stop,interfaces:[])
			executions.add(virtualExecution)
		}
		
		println 'Load Map with used machines '+pmDescriptions.entrySet().size()
		for (Map.Entry<Long,PhysicalMachineAllocationDescription> entry : pmDescriptions.entrySet()) {
			println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
		}
		
		physicalMachineAllocatorService.allocatePhysicalMachines(user,executions.sort(),pms,pmDescriptions)
		ipAllocatorService.allocateIPAddresses(executions.sort())	
			
		for(VirtualMachineExecution execution in executions){
			execution.saveExecution()
		}
		image.virtualMachines.addAll(executions)
		image.save(failOnError:true)
		//if(!Environment.isDevelopmentMode()){
			QueueTaskerControl.addInstancesToDeploy(image,user)
		//}
		
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
	 * Create a task to stop virtual machines executions in list
	 * If state is FAILED changes to FINISHED else to REQUEST_FINISH
	 * @param executions
	 * @return
	 */
	def stopVirtualMachineExecutions(List<VirtualMachineExecution> executions, User requester){
		TreeMap<Deployment, Integer> deployments = new TreeMap<Deployment, Integer>();
		for(VirtualMachineExecution vm in executions){
			if(vm.status.equals(VirtualMachineExecutionStateEnum.FAILED)){				
				vm.finishExecution()
			}else if(vm.status.equals(VirtualMachineExecutionStateEnum.DEPLOYED)){
				vm.status = VirtualMachineExecutionStateEnum.REQUEST_FINISH
				if(deployments.containsKey(vm.deployImage.deployment))deployments.put(vm.deployImage.deployment,deployments.get(vm.deployImage.deployment)+1)
				else deployments.put(vm.deployImage.deployment,1)
			}
		}
		if(deployments.navigableKeySet().size()>0){
			QueueTaskerControl.stopDeployments(deployments.navigableKeySet().toArray(), requester)
		}
	}
	
	/**
	 * Create a task to make a copy from a current execution
	 * @param execution to create a copy from its image
	 * @param user user owner
	 * @param newName name for image copy
	 * @return
	 * @throws Exception
	 */
	//TODO add repository validation
	def createCopy(VirtualMachineExecution execution, User user, String newName)throws Exception{
		if(newName==null||newName.isEmpty())throw new Exception('Image name can not be empty')
		def repository = repositoryService.getMainRepository()
		VirtualMachineImage image = new VirtualMachineImage(name:newName,isPublic:false, fixedDiskSize:execution.deployImage.image.fixedDiskSize,
			user:execution.deployImage.image.user,password:execution.deployImage.image.password,operatingSystem:execution.deployImage.image.operatingSystem,
			accessProtocol:execution.deployImage.image.accessProtocol,imageVersion:1,state:VirtualMachineImageEnum.COPYING,owner:user,repository:repository)
		image.save(failOnError:true)
		execution.putAt("status", VirtualMachineExecutionStateEnum.REQUEST_COPY)
		execution.putAt("message", 'Copy request to image '+image.id)
		QueueTaskerControl.createCopyFromExecution(execution,image,user)
	}
}
