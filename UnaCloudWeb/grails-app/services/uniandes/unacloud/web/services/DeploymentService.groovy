package uniandes.unacloud.web.services

import uniandes.unacloud.common.enums.VirtualMachineExecutionStateEnum;
import uniandes.unacloud.common.utils.RandomUtils;

import uniandes.unacloud.web.services.allocation.IpAllocatorService
import uniandes.unacloud.web.services.allocation.PhysicalMachineAllocatorService
import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.unacloud.web.pmallocators.AllocatorException
import uniandes.unacloud.web.pmallocators.PhysicalMachineAllocationDescription
import uniandes.unacloud.web.queue.QueueTaskerControl;
import uniandes.unacloud.web.utils.java.Hasher;
import uniandes.unacloud.web.domain.Cluster;
import uniandes.unacloud.web.domain.DeployedImage;
import uniandes.unacloud.web.domain.Deployment;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.User;
import uniandes.unacloud.web.domain.VirtualMachineExecution;
import uniandes.unacloud.web.domain.VirtualMachineImage;
import uniandes.unacloud.web.utils.groovy.ImageRequestOptions;

import grails.transaction.Transactional
import grails.util.Environment;

/**
 * This service contains all methods to manage deployment: create and delete cluster.
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
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
		
		
		//Validates that hardware profile is available for user and there are enough host to deploy
		def hwdProfilesAvoided = userRestrictionService.getAvoidHwdProfiles(user)
		requests.eachWithIndex(){ request,i->	
			if(hwdProfilesAvoided.find{it.id==request.hp.id}==null) throw new Exception('Hardware profile does not exist or You don\'t have permissions to use selected one')	
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
		Date start = new java.util.Date()
		Date stop = new java.util.Date(start.getTime()+time)
		requests.eachWithIndex(){ request,i->
			
			def depImage= new DeployedImage(image:request.image,highAvaliavility:request.high,virtualMachines:[])			
			def executions = []
			for(int j=0;j<request.instances;j++){				
				def virtualExecution = new VirtualMachineExecution(deployImage: depImage,name: request.hostname,message: "Initializing",  hardwareProfile: request.hp,disk:0,status: VirtualMachineExecutionStateEnum.QUEUED,startTime: start,stopTime:stop, interfaces:[])
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
		dep.save(failOnError: true,flush:true)		
				
		for(DeployedImage image in images){
			image.deployment = dep
			image.save(failOnError: true,flush:true)
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
	 * Adds new instances in ImageRequestOptions array to a DeployedImage
	 * @param image to adding instances
	 * @param user who request add instances
	 * @param time for execution
	 * @param requests group of deployment properties for executions
	 */
	def synchronized addInstances(DeployedImage image, User user, long time, ImageRequestOptions requestOptions){
		
		Date start = new Date()
		Date stop = new Date(start.getTime()+time)
		
		//Validates that hardware profile is available for user and there are enough host to deploy
		def hwdProfilesAvoided = userRestrictionService.getAvoidHwdProfiles(user)
		if(hwdProfilesAvoided.find{it.id==requestOptions.hp.id}==null)throw new Exception('You don\'t have permissions to use same hardware profile in deployment')		
				
		def labsAvoided = userRestrictionService.getAvoidLabs(user)
		if(labsAvoided.size()==0) throw new Exception('Not enough physical machines available')
		
		List<PhysicalMachine> pms = new ArrayList<>()
		
		labsAvoided.each{
			pms.addAll(it.getAvailableMachines(image.highAvaliavility))
		}
		if(pms.size()==0) throw new Exception('Not enough physical machines available')
		
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptions = physicalMachineAllocatorService.getPhysicalMachineUsage(pms)	
					
		def executions = []
		for(int j=0;j<requestOptions.instances;j++){
			def virtualExecution = new VirtualMachineExecution(deployImage: image,name: requestOptions.hostname,message: "Adding Instance",  hardwareProfile: requestOptions.hp,disk:0,status: VirtualMachineExecutionStateEnum.QUEUED,startTime: new Date(), stopTime:stop,interfaces:[])
			executions.add(virtualExecution)
		}
		
//		println 'Load Map with used machines '+pmDescriptions.entrySet().size()
//		for (Map.Entry<Long,PhysicalMachineAllocationDescription> entry : pmDescriptions.entrySet()) {
//			println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
//		}
		
		physicalMachineAllocatorService.allocatePhysicalMachines(user,executions.sort(),pms,pmDescriptions)
		ipAllocatorService.allocateIPAddresses(executions.sort())	
			
		for(VirtualMachineExecution execution in executions){
			execution.saveExecution()
		}
		image.virtualMachines.addAll(executions)
		image.save(failOnError:true,flush:true)
		if(!Environment.isDevelopmentMode()){
			QueueTaskerControl.addInstancesToDeploy(executions.sort(),user,image)
		}
		
	}
	
	/**
	 * Returns the list of active deployments that owner is different from parameter user
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
	 * Creates a task to stop virtual machines executions in list
	 * If state is FAILED changes to FINISHED else to FINISHING
	 * @param executions
	 * @param user that request stop executions
	 */
	def stopVirtualMachineExecutions(List<VirtualMachineExecution> executions, User requester){
		List<VirtualMachineExecution> executionsToStop = new ArrayList<VirtualMachineExecution>()
		for(VirtualMachineExecution vm : executions){
			if(vm.status.equals(VirtualMachineExecutionStateEnum.FAILED)){				
				vm.finishExecution()
			}else if(vm.status.equals(VirtualMachineExecutionStateEnum.DEPLOYED)){
				vm.putAt("status", VirtualMachineExecutionStateEnum.FINISHING)				
				executionsToStop.add(vm)
			}
		}
		if(executionsToStop.size()>0){
			QueueTaskerControl.stopExecutions(executionsToStop, requester)
		}
	}
	
	/**
	 * Creates a task to make a copy from a current execution
	 * @param execution to create a copy from its image
	 * @param user user owner
	 * @param newName name for image copy
	 * @throws Exception
	 */
	//Validates capacity
	def createCopy(VirtualMachineExecution execution, User user, String newName)throws Exception{
		if(newName==null||newName.isEmpty())throw new Exception('Image name can not be empty')
		if(VirtualMachineImage.where{name==newName&&owner==user}.find())throw new Exception('You have a machine with the same name currently')
		def repository = userRestrictionService.getRepository(user)
		String token = Hasher.hashSha256(newName+new Date().getTime())
		VirtualMachineImage image = new VirtualMachineImage(name:newName,isPublic:false, fixedDiskSize:execution.deployImage.image.fixedDiskSize,
			user:execution.deployImage.image.user,password:execution.deployImage.image.password,operatingSystem:execution.deployImage.image.operatingSystem,
			accessProtocol:execution.deployImage.image.accessProtocol,imageVersion:1,state:VirtualMachineImageEnum.COPYING,owner:user,repository:repository,token:token)
		image.save(failOnError:true,flush:true)
		execution.putAt("status", VirtualMachineExecutionStateEnum.REQUEST_COPY)
		execution.putAt("message", 'Copy request to image '+image.id)
		QueueTaskerControl.createCopyFromExecution(execution,image,execution.deployImage.image,user)
	}
}
