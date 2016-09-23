package uniandes.unacloud.web.pmallocators;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.VirtualMachineExecution;

/**
 * Class to execute First Fit allocator algorithms
 * Assigns all possible virtual machines for each physical machine based in available resources
 * @author Clouder
 *
 */
public class FirstFitAllocator extends VirtualMachineAllocator {
	
	/**
	 * Assigns all possible virtual machines for each physical machine based in available resources
	 */
	@Override
	protected void allocateVirtualMachines(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,final Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException{
		Collections.sort(physicalMachines, new Comparator<PhysicalMachine>() {
			public int compare(PhysicalMachine p1, PhysicalMachine p2) {
				return Long.compare(p1.getDatabaseId(),p2.getDatabaseId());
			}
		});
		vmCycle:for(VirtualMachineExecution vme:virtualMachineList){
			for(PhysicalMachine pm:physicalMachines){
				PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
				if(fitVMonPM(vme, pm, pmad)){
					vme.setExecutionNode(pm);
					if(pmad==null){
						pmad=new PhysicalMachineAllocationDescription(pm.getDatabaseId(),0,0,0);
						physicalMachineDescriptions.put(pmad.getNodeId(),pmad);
					}
					pmad.addResources(vme.getHardwareProfile().getCores(),vme.getHardwareProfile().getRam(), 1);
					continue vmCycle;
				}
			}
			throw new AllocatorException("Cannot allocate all VMs on available insfrastructure");
		}
	}
}
