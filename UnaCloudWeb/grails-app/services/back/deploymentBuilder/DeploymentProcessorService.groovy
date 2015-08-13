package back.deploymentBuilder;

import javax.websocket.DeploymentException

import org.springframework.aop.ThrowsAdvice;

import back.allocators.IpAllocatorService;
import back.allocators.PhysicalMachineAllocatorService;
import back.pmallocators.AllocatorException;
import back.userRestrictions.UserRestrictionException;
import back.userRestrictions.UserRestrictionProcessorService;
import unacloud2.DeployedCluster;
import unacloud2.DeployedImage
import unacloud2.PhysicalMachine;
import unacloud2.User;
import unacloud2.VirtualMachineExecution;
import unacloud2.enums.PhysicalMachineStateEnum;

class DeploymentProcessorService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of user restriction validator service
	 */
	
	UserRestrictionProcessorService userRestrictionProcessorService
	
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
	 * Builds a deployment of an image. Validates user restrictions of the desired
	 * deployment
	 * @param image Deployed image with the virtual machines to be deployed
	 * @param user session user for permission validation
	 * @param addInstancesDeployment indicates if the deployment is new or 
	 * add instance type 
	 * @throws UserRestrictionException if the user cannot make the deployment built
	 * @throws AllocatorException if cannot assign a physical machine or an IP 
	 * to any virtual machine
	 */
	
	def doDeployment(DeployedImage image,User user,boolean addInstancesDeployment) throws UserRestrictionException, AllocatorException{
		
		/*
		 * Selects the machines to be allocated and validated depending on 
		 * addInstancesDeployment parameter
		 */
		
		ArrayList<VirtualMachineExecution> vms=new ArrayList<>();
		if(!addInstancesDeployment)
		 vms.addAll(image.virtualMachines);
		
		else{
			println "adding only new instances for allocation"
			def virtualMachines= new ArrayList<>()
			for(VirtualMachineExecution vm:image.virtualMachines)
			if(vm.message.equals("Adding instance")){
				virtualMachines.add(vm)
			}
			vms.addAll(virtualMachines)
		}
		
		/*
		 * selects the physical machine list and applies user restrictions
		 */
		List<PhysicalMachine> pms
		pms=PhysicalMachine.findAllWhere(state:PhysicalMachineStateEnum.ON,highAvailability: image.highAvaliavility);
		println "tamaño en doDeployment:"+pms.size()
		userRestrictionProcessorService.applyUserPermissions(user,vms,pms)
		println "tamaño después de userRestriction:"+pms.size()
		
		/*
		 * Allocates a physical machine and an IP to every selected virtual machine
		 */
		if(pms.size()==0) throw new AllocatorException('No physical machines available') 
		
		physicalMachineAllocatorService.allocatePhysicalMachines(vms,pms,addInstancesDeployment)
		ipAllocatorService.allocateIPAddresses(image, addInstancesDeployment)
		
		//Code to show allocation results on screen
		println '******RESULTS******'
		for(VirtualMachineExecution vm: image.virtualMachines){			
		    println vm.id +'-'+vm.name+' '+vm.executionNode.name;		 
		}	
		
	}
	
	def doHeterogeneousDeployment(DeployedCluster cluster,User user,boolean addInstancesDeployment) throws UserRestrictionException, AllocatorException{
		
		/*
		 * Selects the machines to be allocated and validated depending on
		 * addInstancesDeployment parameter
		 */
		
		ArrayList<VirtualMachineExecution> vms=new ArrayList<>();
		for(image in cluster.images){
			if(!addInstancesDeployment)
			 vms.addAll(image.virtualMachines);
			
			else{
				println "adding only new instances for allocation"
				def virtualMachines= new ArrayList<>()
				for(VirtualMachineExecution vm:image.virtualMachines)
				if(vm.message.equals("Adding instance")){
					virtualMachines.add(vm)
				}
				vms.addAll(virtualMachines)
			}
		}
		println "Preparo un total de "+vms.size()
		/*
		 * selects the physical machine list and applies user restrictions
		 */
		List<PhysicalMachine> pms
		pms=PhysicalMachine.findAllWhere(state:PhysicalMachineStateEnum.ON,highAvailability: false);
		println "tamaño en doDeployment:"+pms.size()
		userRestrictionProcessorService.applyUserPermissions(user,vms,pms)
		println "tamaño después de userRestriction:"+pms.size()
		
		/*
		 * Allocates a physical machine and an IP to every selected virtual machine
		 */
		if(pms.size()==0) throw new AllocatorException('No physical machines available')
		
		physicalMachineAllocatorService.allocatePhysicalMachines(vms,pms,addInstancesDeployment)
		for(image in cluster.images)
		ipAllocatorService.allocateIPAddresses(image, addInstancesDeployment)
		
		println '******RESULTS******'
		for(image in cluster.images)
		for(VirtualMachineExecution vm: image.virtualMachines){
			println vm.id +'-'+vm.name+' '+vm.executionNode.name;
		}
	}
}
