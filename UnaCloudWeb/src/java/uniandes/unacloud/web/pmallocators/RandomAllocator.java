package uniandes.unacloud.web.pmallocators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.VirtualMachineExecution;

/**
 * Class to execute Random allocator algorithms
 * Assigns a virtual machine execution to a physical machine based in random number.
 * @author Clouder
 *
 */
public class RandomAllocator extends VirtualMachineAllocator {

	/**
	 * Assigns a virtual machine execution to a physical machine based in random number.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void allocateVirtualMachines(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,Map<Long,PhysicalMachineAllocationDescription> physicalMachineDescriptions) throws AllocatorException{
		if(virtualMachineList.size()>2*physicalMachines.size()){
		}else{
			Collections.shuffle(virtualMachineList);
			
			for(int e=0;e<virtualMachineList.size();e++){
				VirtualMachineExecution vm= virtualMachineList.get(e);
				Collections.shuffle(physicalMachines);
				for (Iterator iterator = physicalMachines.iterator(); iterator
						.hasNext();) {
					PhysicalMachine physicalMachine = (PhysicalMachine) iterator
							.next();
					PhysicalMachineAllocationDescription pmad= physicalMachineDescriptions.get(physicalMachine.getDatabaseId());
					if (fitVMonPM(virtualMachineList.get(e), physicalMachine, pmad)){
						vm.setExecutionNode(physicalMachine);
						if(pmad==null){
							pmad=new PhysicalMachineAllocationDescription(physicalMachine.getDatabaseId(),0,0,0);
							physicalMachineDescriptions.put(pmad.getNodeId(),pmad);
						}
						pmad.addResources(vm.getHardwareProfile().getCores(),vm.getHardwareProfile().getRam(), 1);
						break;
					
					}
					if (vm.getExecutionNode()==null){
						throw new AllocatorException("Cannot allocate all VMs on available insfrastructure");
					}
				}
				
			}
		}
		
	}

}
