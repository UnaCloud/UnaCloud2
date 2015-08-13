package back.allocators

import back.pmallocators.AllocatorException
import back.services.PhysicalMachineStateManagerService;

import java.util.Comparator;

import unacloud2.*
import unacloudEnums.VirtualMachineExecutionStateEnum;


class IpAllocatorService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Allocates IP addresses to all virtual machines in a deployed image  
	 * @param image Deployed image which virtual machines will be allocated with an 
	 * IP 
	 * @param addInstancesDeployment indicates if the deployment is new or added 
	 * instances type
	 * @return 
	 */
	
	def allocateIPAddresses(DeployedImage image, boolean addInstancesDeployment){
		// if the image is new it will simply allocate all machines with an IP
		if(!addInstancesDeployment){
			for(VirtualMachineExecution vme in image.virtualMachines){
				IPPool ipPool= vme.executionNode.laboratory.virtualMachinesIPs
				println "Using node ip "+vme.executionNode.ip.ip
				
				/*
				 * For each machine it searches an empty IP in the physical 
				 * machine IP pool and assigns it to the virtual machine
				 */
				
				for(ip in ipPool.ips){
					println "Verifying IP "+ ip.ip+" used: "+ip.used
					if(ip.used==false){
						println "Using vm ip "+ip.ip
						vme.putAt("ip", ip)
						ip.used=true
						String[] subname= ip.ip.split("\\.")
						vme.putAt("name", vme.name+subname[2]+subname[3])
						break
					}
				}
				/*
				 * After allocation it verifies if the machine actually has an IP.
				 * If not it makes a rollback of changes and throws the exception
				 */
				if (vme.ip==null){ 
					for(VirtualMachineExecution vm in image.virtualMachines){
						if(vm.ip!=null)	vm.ip.used=false
					}
					throw new AllocatorException("Not enough IPs for this deployment")
				}
			}
			
		}
		
		/* 
		 * if the deployment contains added instances it will make the same process
		 * only assigning an IP to the new machines
		*/
		
		else{
			for(VirtualMachineExecution vme in image.virtualMachines){
				if(vme.message.equals("Adding instance")&& vme.status.equals(VirtualMachineExecutionStateEnum.DEPLOYING)){
					IPPool ipPool= vme.executionNode.laboratory.virtualMachinesIPs
					println "Using node ip "+vme.executionNode.ip.ip
					for(ip in ipPool.ips){
						if(ip.used==false){
							vme.putAt("ip", ip)
							ip.used=true
							String[] subname= ip.ip.split("\\.")
							vme.putAt("name", vme.name+subname[2]+subname[3]) 
							break
						}
					}
					if (vme.ip==null){ 
						for(VirtualMachineExecution vm in image.virtualMachines){
							if(vm.ip!=null)	vm.ip.used=false
						}
						throw new AllocatorException("Not enough IPs for this deployment")
					}
				}
			}
		}
	}		
	
}
