package back.pmallocators;

import java.util.List;
import java.util.Map;

import unacloud2.PhysicalMachine;
import unacloud2.VirtualMachineExecution;

public class SingletonAllocator extends VirtualMachineAllocator{

	@Override
	protected void allocateVirtualMachines(
			List<VirtualMachineExecution> virtualMachineList,
			List<PhysicalMachine> physicalMachines,
			Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions)
			throws AllocatorException {
			for(PhysicalMachine pm: physicalMachines){
				PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
				if (pmad == null){
					for (VirtualMachineExecution vm : virtualMachineList){
						if(fitVMonPM(vm, pm, pmad)){
							vm.setExecutionNode(pm);
							if(pmad==null){
								pmad=new PhysicalMachineAllocationDescription(pm.getDatabaseId(),0,0,0);
								physicalMachineDescriptions.put(pmad.getNodeId(),pmad);
							}
							pmad.addResources(vm.getHardwareProfile().getCores(),vm.getHardwareProfile().getRam(), 1);
							System.out.println("Despues: "+pmad);
						}
						else throw new AllocatorException("Cannot allocate all VMs on a single machine");	
					}
					break;
				}
				
			}
	}

}
