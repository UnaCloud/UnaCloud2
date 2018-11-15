package uniandes.unacloud.web.pmallocators;

import java.util.List;
import java.util.Map;

import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;

/**
 * Class to execute Singleton allocator algorithms
 * Assigns only one execution for each physical machine
 * @author Clouder
 *
 */
public class SingletonAllocator extends ExecutionAllocator {

	/**
	 * Assigns only one execution for each physical machine
	 */
	@Override
	protected void allocateExecutions(List<Execution> executionList, List<PhysicalMachine> physicalMachines, 
			Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions) throws AllocatorException {
		System.out.println("Start allocations");
		// start with the first physical machine and the first virtual machine
		int nextPm = 0;
		int nextVm = 0;

		// while there are physical and virtual machines
		System.out.println("While there are phy and vms");
		while (nextPm < physicalMachines.size() && nextVm < executionList.size()) {
						
			// get the physical and virtual machines
			PhysicalMachine pm = physicalMachines.get(nextPm);
			Execution vm = executionList.get(nextVm);
			
			// get the machine description
			PhysicalMachineAllocationDescription pmad = physicalMachineDescriptions.get(pm.getDatabaseId());
			if (pmad == null) {
				
				// if the physical machine can run the vm
				if (fitEXonPM(vm, pm, pmad)) {
						
					// assign the vm to the virtual machine
					vm.setExecutionNode(pm);
					if (pmad == null) {
						pmad = new PhysicalMachineAllocationDescription(pm.getDatabaseId(), 0, 0, 0);
						physicalMachineDescriptions.put(pmad.getNodeId(), pmad);
					}
					// increase the resources used in that machine
					pmad.addResources(vm.getHardwareProfile().getCores(), vm.getHardwareProfile().getRam(), 1);
					
					// get the next VM
					nextVm++;
					// use the next PM
					nextPm++;
				
				// if the physical machine cannot run the vm
				} else {
					// try the next PM
					nextPm++;
				}				
			}
			else //Hypothesis it is getting stuck on the same physical machine that already possesses an execution
			{
				nextPm++;
			}
		}
		System.out.println("Finish allocation");

		// ends the cycle when:
		//   all the VMs have been allocated
		//   all the PMs have been tested
		
		// is there a non-allocated VM ?
		if (nextVm < executionList.size()) {
			throw new AllocatorException("Cannot allocate all Executions on separated machines");
		}
		
	}

}
