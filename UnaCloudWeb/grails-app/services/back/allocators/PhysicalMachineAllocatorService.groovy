package back.allocators

import groovy.sql.Sql
import unacloud2.*
import unacloud2.enums.PhysicalMachineStateEnum;
import unacloudEnums.VirtualMachineExecutionStateEnum;
import back.pmallocators.AllocatorEnum
import back.pmallocators.PhysicalMachineAllocationDescription

class PhysicalMachineAllocatorService {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Representation of datasource in order to make queries 
	 */
	
	javax.sql.DataSource dataSource
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Allocates the virtual machines' list in the given physical machines' list 
	 * @param vms list of virtual machines
	 * @param pms list of physical machines
	 * @param addInstancesDeployment indicates if the deployment is new or 
	 * add instance type 
	 */
	
	def allocatePhysicalMachines( ArrayList<VirtualMachineExecution> vms,List<PhysicalMachine> pms,boolean addInstancesDeployment){
		
		/*
		 * Finds the allocation method set in server variables
		 */
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptions = getPhysicalMachineUsage()
		//println vms
		ServerVariable allocatorName=ServerVariable.findByName("VM_ALLOCATOR_NAME");
		AllocatorEnum allocator=AllocatorEnum.ROUND_ROBIN;
		try{
			if(allocatorName!=null){
				AllocatorEnum allocEnum=AllocatorEnum.valueOf(allocatorName.getVariable());
				if(allocEnum!=null)allocator=allocEnum;
			}
		}
		catch(Exception ex){
		}
		/*
		 * Calls the correspondent allocation method 
		 */
		allocator.getAllocator().startAllocation(vms,pms,pmDescriptions);
	}
	
	
	/**
	 * Allocates a single virtual machine in a random physical machine
	 * @param vme virtual machine to be allocated
	 */
	
	def allocatePhysicalMachine(VirtualMachineExecution vme ){
		List<PhysicalMachine> l=PhysicalMachine.findAllByState(PhysicalMachineStateEnum.ON);
		Collections.sort(l,new Comparator<PhysicalMachine>(){
					public int compare(PhysicalMachine p1,PhysicalMachine p2){
						return Long.compare(p1.id,p2.id);
					}
				});
		vme.executionNode = l.first();
	}
	
	/**
	 * Calculates the usage of the infrastructure
	 * @return pmDescriptions map with information of every physical machine 
	 * remaining capacity
	 */
	
	def getPhysicalMachineUsage(){
		def sql = new Sql(dataSource)
		Map<Long,PhysicalMachineAllocationDescription> pmDescriptions=new TreeMap<>();
		
		sql.eachRow('select execution_node_id,count(*) as vms,sum(ram) as ram,sum(cores) as cores from virtual_machine_execution join hardware_profile on virtual_machine_execution.hardware_profile_id= hardware_profile.id where status != \''+VirtualMachineExecutionStateEnum.FINISHED+'\' group by execution_node_id'){ row ->
			if(row.execution_node_id!=null)pmDescriptions.put(row.execution_node_id, new PhysicalMachineAllocationDescription(row.execution_node_id,row.cores.toInteger(),row.ram.toInteger(),row.vms.toInteger()));
		}
		println 'Load Map with used machines '+pmDescriptions.entrySet().size()
		for (Map.Entry<Long,PhysicalMachineAllocationDescription> entry : pmDescriptions.entrySet()) {
			println("Key: " + entry.getKey() + ". Value: " + entry.getValue());
	    }
		return pmDescriptions;
	}
}
