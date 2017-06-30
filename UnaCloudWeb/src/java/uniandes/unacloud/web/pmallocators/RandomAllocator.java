package uniandes.unacloud.web.pmallocators;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;

/**
 * Class to execute Random allocator algorithms
 * Assigns an execution to a physical machine based in random number.
 * @author Clouder
 *
 */
public class RandomAllocator extends ExecutionAllocator {

	/**
	 * Assigns an execution to a physical machine based in random number.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void allocateExecutions(List<Execution> executionList,List<PhysicalMachine> physicalMachines, Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions) throws AllocatorException {
		
		if (executionList.size() <= 2 * physicalMachines.size()) {
			Collections.shuffle(executionList);
			
			for (int e = 0; e < executionList.size(); e++) {
				Execution vm = executionList.get(e);
				Collections.shuffle(physicalMachines);
				for (Iterator iterator = physicalMachines.iterator(); iterator.hasNext();) {
					PhysicalMachine physicalMachine = (PhysicalMachine) iterator.next();
					PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(physicalMachine.getDatabaseId());
					if (fitEXonPM(executionList.get(e), physicalMachine, pmad)) {
						vm.setExecutionNode(physicalMachine);
						if (pmad == null) {
							pmad = new PhysicalMachineAllocationDescription(physicalMachine.getDatabaseId(), 0, 0, 0);
							physicalMachineDescriptions.put(pmad.getNodeId(),pmad);
						}
						pmad.addResources(vm.getHardwareProfile().getCores(), vm.getHardwareProfile().getRam(), 1);
						break;
					
					}
					if (vm.getExecutionNode() == null) {
						throw new AllocatorException("Cannot allocate all Executions on available insfrastructure");
					}
				}
				
			}
		}
		
	}

}
