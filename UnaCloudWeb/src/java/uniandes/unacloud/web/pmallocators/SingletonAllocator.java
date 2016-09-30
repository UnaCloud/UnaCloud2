package uniandes.unacloud.web.pmallocators;

import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;

/**
 * Class to execute Singleton allocator algorithms
 * Assigns only one virtual execution for each physical machine
 * @author Clouder
 *
 */
public class SingletonAllocator extends VirtualMachineAllocator{

	/**
	 * Assigns only one virtual execution for each physical machine
	 */
	@Override
	protected void allocateVirtualMachines(
			List<Execution> virtualMachineList,
			List<PhysicalMachine> physicalMachines,
			Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions)
			throws AllocatorException {
			for(PhysicalMachine pm: physicalMachines){
				PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
				if (pmad == null){
					for (Execution vm : virtualMachineList){
						if(fitVMonPM(vm, pm, pmad)){
							vm.setExecutionNode(pm);
							if(pmad==null){
								pmad=new PhysicalMachineAllocationDescription(pm.getDatabaseId(),0,0,0);
								physicalMachineDescriptions.put(pmad.getNodeId(),pmad);
							}
							pmad.addResources(vm.getHardwareProfile().getCores(),vm.getHardwareProfile().getRam(), 1);
						}
						else throw new AllocatorException("Cannot allocate all VMs on a single machine");	
					}
					break;
				}
				
			}
	}

}
