package uniandes.unacloud.web.services.allocation

import uniandes.unacloud.common.enums.ExecutionStateEnum;

import grails.transaction.Transactional
import groovy.sql.Sql

import uniandes.unacloud.web.services.HardwareProfileService;
import uniandes.unacloud.web.domain.PhysicalMachine
import uniandes.unacloud.web.domain.User
import uniandes.unacloud.web.services.UserRestrictionService
import uniandes.unacloud.web.domain.Execution
import uniandes.unacloud.web.pmallocators.AllocatorEnum
import uniandes.unacloud.web.pmallocators.PhysicalMachineAllocationDescription

/**
 * This service is only for process.
 * Service used to determinate Physical Machine allocation for executions.
 * This class should be a service to use hibernate connection.
 * @author CesarF
 *
 */
@Transactional
class PhysicalMachineAllocatorService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of User restriction services
	 */
	
	UserRestrictionService userRestrictionService	

	/**
	 * Representation of datasource in order to make queries
	 */
	
	javax.sql.DataSource dataSource
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Allocates the execution list in the given physical machines' list
	 * @param vms list of executions
	 * @param pms list of physical machines
	 * @param pmdDescriptions map with descriptions of executions to be deployed
	 */
	
	def allocatePhysicalMachines(User user, List<Execution> vms,List<PhysicalMachine> pms, Map<Long,PhysicalMachineAllocationDescription> pmDescriptions){
		AllocatorEnum allocator = userRestrictionService.getAllocator(user)	
		allocator.getAllocator().startAllocation(vms,pms,pmDescriptions);
	}
	
	/**
	 * Calculates the usage of the infrastructure and return a list of available resources
	 * @param physical machine list
	 * @return pmDescriptions map with information of every physical machine
	 * remaining capacity
	 */	
	def getPhysicalMachineUsage(List<PhysicalMachine> pms){
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptions=new TreeMap<>();
		
		if(pms.size()==0)return pmDescriptions
		
		String listId = ""
		for(int i = 0; i<pms.size();i++){
			listId+=pms[i].id
			if(i!=pms.size()-1)listId+=","
		}
		def sql = new Sql(dataSource)
		
		sql.eachRow('select execution_node_id,count(*) as vms,sum(ram) as ram,sum(cores) as cores from execution join hardware_profile on execution.hardware_profile_id= hardware_profile.id where status != \''+ExecutionStateEnum.FINISHED+'\' and execution_node_id in ('+listId+') group by execution_node_id'){ row ->
			if(row.execution_node_id!=null)pmDescriptions.put(row.execution_node_id, new PhysicalMachineAllocationDescription(row.execution_node_id,row.cores.toInteger(),row.ram.toInteger(),row.vms.toInteger()));
		}
		return pmDescriptions;
	}
  
}
