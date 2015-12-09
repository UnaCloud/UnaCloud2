package unacloud.allocation

import grails.transaction.Transactional
import unacloud.DeployedImage
import unacloud.ExecutionIP
import unacloud.NetInterface
import unacloud.VirtualMachineExecution
import unacloud.enums.IPEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.pmallocators.AllocatorException

@Transactional
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
	//TODO manage net interfaces configuration
	
	def allocateIPAddresses(virtualExecutions){
		
		for(VirtualMachineExecution vme in virtualExecutions){
			if(vme.status.equals(VirtualMachineExecutionStateEnum.REQUESTED)){
				List <ExecutionIP> ips= vme.executionNode.laboratory.getAvailableIps()
				for(ip in ips){
					if(ip.state==IPEnum.AVAILABLE){
						NetInterface netInterface = new NetInterface(name:'eth0',ip:ip,virtualExecution:vme)
						vme.interfaces.add(netInterface)
						ip.putAt('state',IPEnum.RESERVED)
						String[] subname= ip.ip.split("\\.")
						vme.setName(vme.name+subname[2]+subname[3]) 
						break
					}
				}
				if (vme.interfaces.size()==0){ 
					for(VirtualMachineExecution vm in virtualExecutions){
						if(vme.status.equals(VirtualMachineExecutionStateEnum.REQUESTED)){
							for(NetInterface net in vme.interfaces){
								net.ip.putAt('state',IPEnum.AVAILABLE)
							}
						}						
					}
					throw new AllocatorException("Not enough IPs for this deployment")
				}
			}
		}
	}		
}
