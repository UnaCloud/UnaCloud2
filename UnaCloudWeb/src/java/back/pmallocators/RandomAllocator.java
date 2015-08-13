package back.pmallocators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import unacloud2.PhysicalMachine;
import unacloud2.VirtualMachineExecution;

public class RandomAllocator extends VirtualMachineAllocator {

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
