package uniandes.unacloud.web.pmallocators;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.VirtualMachineExecution;

/**
 * Class to execute Round Robin allocator algorithms
 * Assigns a virtual machine for each physical machine order by physical machine id
 * @author Clouder
 *
 */
public class RoundRobinAllocator extends VirtualMachineAllocator {

	/**
	 * Assigns a virtual machine for each physical machine order by physical machine id
	 */
	@Override
	protected void allocateVirtualMachines(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException{
		Collections.sort(physicalMachines, new Comparator<PhysicalMachine>() {
			public int compare(PhysicalMachine p1, PhysicalMachine p2) {
				return Long.compare(p1.getDatabaseId(), p2.getDatabaseId());
			}
		});
		ciclo1:for (int nextVm = 0, lastNextVm = 0; nextVm < virtualMachineList.size();) {
			for (PhysicalMachine pm : physicalMachines) {
				if (nextVm >= virtualMachineList.size())break ciclo1;
				PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
				VirtualMachineExecution nextVirtualMachine = virtualMachineList.get(nextVm);
				if(fitVMonPM(nextVirtualMachine, pm, pmad)){
					nextVirtualMachine.setExecutionNode(pm);
					if(pmad==null){
						pmad=new PhysicalMachineAllocationDescription(pm.getDatabaseId(),0,0,0);
						physicalMachineDescriptions.put(pmad.getNodeId(),pmad);
					}
					pmad.addResources(nextVirtualMachine.getHardwareProfile().getCores(),nextVirtualMachine.getHardwareProfile().getRam(), 1);
					nextVm++;
				}
			}
			if (lastNextVm == nextVm) {
				throw new AllocatorException("Cannot allocate all VMs on available insfrastructure");
			}
			lastNextVm = nextVm;
		}
	}
}
