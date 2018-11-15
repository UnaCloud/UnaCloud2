package uniandes.unacloud.web.pmallocators;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;

/**
 * Class to execute Round Robin allocator algorithms
 * Assigns an execution for each physical machine order by physical machine id
 * @author Clouder
 *
 */
public class RoundRobinAllocator extends ExecutionAllocator {

	/**
	 * Assigns an execution for each physical machine order by physical machine id
	 */
	@Override
	protected void allocateExecutions(List<Execution> executionList, List<PhysicalMachine> physicalMachines, Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions) throws AllocatorException {
		Collections.sort(physicalMachines, new Comparator<PhysicalMachine>() {
			public int compare(PhysicalMachine p1, PhysicalMachine p2) {
				return p1.getName().compareTo(p2.getName());
			}
		});
		System.out.println("\t Start round robin");
		ciclo1 : for (int nextVm = 0, lastNextVm = 0; nextVm < executionList.size();) {
			for (PhysicalMachine pm : physicalMachines) {
				if (nextVm >= executionList.size())
					break ciclo1;
				PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
				Execution nextExecution = executionList.get(nextVm);
				System.out.println("\t Perfect fit "+fitEXonPM(nextExecution, pm, pmad));
				if (fitEXonPM(nextExecution, pm, pmad)) {
					nextExecution.setExecutionNode(pm);
					if (pmad == null) {
						pmad = new PhysicalMachineAllocationDescription(pm.getDatabaseId(), 0, 0, 0);
						physicalMachineDescriptions.put(pmad.getNodeId(), pmad);
					}
					pmad.addResources(nextExecution.getHardwareProfile().getCores(), nextExecution.getHardwareProfile().getRam(), 1);
					nextVm++;
				}
			}
			if (lastNextVm == nextVm) {
				throw new AllocatorException("Cannot allocate all Executions on available insfrastructure");
			}
			lastNextVm = nextVm;
		}
	}
}
