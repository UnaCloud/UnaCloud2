package uniandes.unacloud.web.pmallocators;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;

/**
 * Class to execute Best Fit allocator algorithms
 * It sorts physical machines based in available resources, assigns an execution in first machine in list and sorts again.
 * @author Clouder
 *
 */
public class FirstFitDecreasingAllocator extends ExecutionAllocator {
	
	/**
	 * Class which implements Comparator
	 * Compare physical machines base in cores
	 * @author Clouder
	 *
	 */
	public class PhysicalMachineComparator implements Comparator<PhysicalMachine> {
		/**
		 * Physical machines information
		 */
		Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions;
		
		public PhysicalMachineComparator(Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions) {
			this.physicalMachineDescriptions = physicalMachineDescriptions;
		}
		
		/**
		 * Compares physical machines, return cores available.
		 */
		public int compare(PhysicalMachine p1, PhysicalMachine p2) {
			PhysicalMachineAllocationDescription pmad1 = physicalMachineDescriptions.get(p1.getDatabaseId());
			PhysicalMachineAllocationDescription pmad2 = physicalMachineDescriptions.get(p2.getDatabaseId());
			int coresUsados1 = pmad1 == null ? 0 : pmad1.getCores();
			int coresUsados2 = pmad2 == null ? 0 : pmad2.getCores();
			int cores = p1.getCores() - coresUsados1 - (p2.getCores() - coresUsados2);
			return cores;
		}
	}
	/**
	 * It sorts physical machines based in available resources, assigns an execution in first machine in list and sorts again.
	 */
	@Override
	protected void allocateExecutions(List<Execution> executionList, List<PhysicalMachine> physicalMachines, final Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions) throws AllocatorException {
		Collections.sort(physicalMachines, new PhysicalMachineComparator(physicalMachineDescriptions));
		Collections.sort(executionList, new Comparator<Execution>() {
			public int compare(Execution v1, Execution v2) {
				return Integer.compare(v2.getHardwareProfile().getCores(), v1.getHardwareProfile().getCores());
			}
		});
		vmCycle : for (Execution vme : executionList) {
			for (PhysicalMachine pm : physicalMachines) {
				PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
				if (fitEXonPM(vme, pm, pmad)) {
					vme.setExecutionNode(pm);
					if (pmad == null) {
						pmad=new PhysicalMachineAllocationDescription(pm.getDatabaseId(),0,0,0);
						physicalMachineDescriptions.put(pmad.getNodeId(), pmad);
					}
					pmad.addResources(vme.getHardwareProfile().getCores(), vme.getHardwareProfile().getRam(), 1);
					continue vmCycle;
				}
			}
			throw new AllocatorException("Cannot allocate all Executions on available insfrastructure");
		}
	}
}
