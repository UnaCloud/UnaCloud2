package back.pmallocators;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import unacloud2.PhysicalMachine;
import unacloud2.VirtualMachineExecution;

public class RoundRobinAllocator extends VirtualMachineAllocator {
	@SuppressWarnings("unused")
	private static final int MAX_VM_PER_PM = 3;

	@Override
	protected void allocateVirtualMachines(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException{
		Collections.sort(physicalMachines, new Comparator<PhysicalMachine>() {
			public int compare(PhysicalMachine p1, PhysicalMachine p2) {
				return Long.compare(p1.getDatabaseId(), p2.getDatabaseId());
			}
		});
		ciclo1:for (int nextVm = 0, lastNextVm = 0; nextVm < virtualMachineList.size();) {
			for (PhysicalMachine pm : physicalMachines) {
				System.out.println("evaluating machine: "+pm.getName());
				if (nextVm >= virtualMachineList.size())break ciclo1;
				PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
				VirtualMachineExecution nextVirtualMachine = virtualMachineList.get(nextVm);
				System.out.println("Antes:"+pmad);
				System.out.println("Fitin vm:"+nextVirtualMachine.getHardwareProfile().getCores()+" "+nextVirtualMachine.getHardwareProfile().getRam());
				if(fitVMonPM(nextVirtualMachine, pm, pmad)){
					nextVirtualMachine.setExecutionNode(pm);
					if(pmad==null){
						pmad=new PhysicalMachineAllocationDescription(pm.getDatabaseId(),0,0,0);
						physicalMachineDescriptions.put(pmad.getNodeId(),pmad);
					}
					pmad.addResources(nextVirtualMachine.getHardwareProfile().getCores(),nextVirtualMachine.getHardwareProfile().getRam(), 1);
					System.out.println("Despues: "+pmad);
					nextVm++;
				}else{
					System.out.println("No hace fit");
				}
			}
			if (lastNextVm == nextVm) {
				throw new AllocatorException("Cannot allocate all VMs on available insfrastructure");
			}
			lastNextVm = nextVm;
		}
	}
}
