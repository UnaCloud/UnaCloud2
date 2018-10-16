package uniandes.unacloud.web.services.allocation

import grails.transaction.Transactional
import uniandes.unacloud.web.domain.DeployedImage
import uniandes.unacloud.web.domain.ExecutionIP
import uniandes.unacloud.web.domain.NetInterface
import uniandes.unacloud.web.domain.Execution
import uniandes.unacloud.web.pmallocators.AllocatorException
import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.IPEnum;

/**
 * This service is only for process.
 * Service used to determinate IP allocation for executions.
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
	 * Allocates IP addresses to all executions in parameters
	 * @param list of executions
	 * @return List of ids of reserved ips for restoring database state
	**/
	//TODO manage net interfaces configuration	
	def allocateIPAddresses(executions){
		//Get all the reserved ips of the executions
		def reservedIps=[]
		for (Execution vme in executions) {
			if (vme.state.state.equals(ExecutionStateEnum.REQUESTED)) {
				List <ExecutionIP> ips = vme.executionNode.laboratory.getAvailableIps()
				for (ip in ips) {
					if (ip.state == IPEnum.AVAILABLE) {
						NetInterface netInterface = new NetInterface(name:'eth0', ip:ip, execution:vme)
						vme.interfaces.add(netInterface)
						ip.putAt('state', IPEnum.RESERVED)
						reservedIps.add(ip.id)
						String[] subname = ip.ip.split("\\.")
						vme.setName(vme.name + subname[2] + subname[3]) 
						break
					}
				}
				if (vme.interfaces.size() == 0) { 
					for (Execution vm in executions)
						if (vme.state.state.equals(ExecutionStateEnum.REQUESTED))
							for (NetInterface net in vme.interfaces)
								net.ip.putAt('state',IPEnum.AVAILABLE)					
					throw new AllocatorException("Not enough IPs for this deployment")
				}
			}
		}
		return reservedIps
	}		
}
