package uniandes.unacloud.web.services.allocation

import uniandes.unacloud.common.enums.VirtualMachineExecutionStateEnum;

import grails.transaction.Transactional
import uniandes.unacloud.web.domain.DeployedImage
import uniandes.unacloud.web.domain.ExecutionIP
import uniandes.unacloud.web.domain.NetInterface
import uniandes.unacloud.web.domain.VirtualMachineExecution
import uniandes.unacloud.web.pmallocators.AllocatorException
import uniandes.unacloud.share.enums.IPEnum;

/**
 * This service is only for process.
 * Service used to determinate IP allocation for virtual executions.
 * This class should be a service to use hibernate connection.
 * @author CesarF
 *
 */
@Transactional
class IpAllocatorService {

    //-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Allocates IP addresses to all virtual machines in parameters
	 * @param list of virtual machines executions
	**/
	//TODO manage net interfaces configuration	
	def allocateIPAddresses(virtualExecutions){		
		for(VirtualMachineExecution vme in virtualExecutions){
			if(vme.status.equals(VirtualMachineExecutionStateEnum.QUEUED)){
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
						if(vme.status.equals(VirtualMachineExecutionStateEnum.QUEUED)){
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