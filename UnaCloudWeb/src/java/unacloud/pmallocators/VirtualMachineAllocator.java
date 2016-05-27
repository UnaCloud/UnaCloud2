package unacloud.pmallocators;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import unacloud.PhysicalMachine;
import unacloud.VirtualMachineExecution;

/**
 * Abstract class with main methods to allocate deployments. Validates enough resources in physical machine and enough IPs in lab
 * The purpose of this class is to be extended to code allocator algorithms 
 * @author Cloder
 *
 */
public abstract class VirtualMachineAllocator{
	
	private TreeMap<Long, Integer> ipsNeeded;
	
	public VirtualMachineAllocator() {
		ipsNeeded = new TreeMap<Long, Integer>();
	}
	
	/**
	 * Start the allocation process
	 * @param virtualMachineList
	 * @param physicalMachines
	 * @param physicalMachineDescriptions
	 * @throws AllocatorException
	 */
	public synchronized void startAllocation(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,Map<Long,PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException{
		ipsNeeded = new TreeMap<Long, Integer>();
		allocateVirtualMachines(virtualMachineList, physicalMachines, physicalMachineDescriptions);
	}

	/**
	 * Method to match physical machines with virtual machines execution.
	 * @param virtualMachineList
	 * @param physicalMachines
	 * @param physicalMachineDescriptions
	 * @throws AllocatorException
	 */
	protected abstract void allocateVirtualMachines(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,Map<Long,PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException;
	
	/**
	 * validates if an execution fits with resources of a physical machine
	 * @param vme
	 * @param pm
	 * @param pmad
	 * @return true if there is enough resources in physical machine to assign virtual execution 
	 */
	protected boolean fitVMonPM(VirtualMachineExecution vme,PhysicalMachine pm,PhysicalMachineAllocationDescription pmad){
		
		System.out.println("Required: vm cores"+vme.getHardwareProfile().getCores()+"vm ram"+ vme.getHardwareProfile().getRam()+" pm cores"+pm.getCores()+"pm ram"+ pm.getRam());	
		System.out.println("Used "+pmad);
		if (pmad == null && vme.getHardwareProfile().getCores() <= pm.getCores() && vme.getHardwareProfile().getRam() <= pm.getRam())
			return isThereEnoughIps(pm);
		else if (pmad!= null && pmad.getCores() + vme.getHardwareProfile().getCores() <= pm.getCores()&& pmad.getRam() + vme.getHardwareProfile().getRam() <= pm.getRam() && pmad.getVms()+1 <= pm.getpCores())
			return isThereEnoughIps(pm);
		else return false;
	}
	/**
	 * Validates if there are enough IP to assign another execution in a physical machine
	 * @param pm Physical machine to evaluate
	 * @return true if there is a IP available for physical machines, false in case or not
	 */
	private boolean isThereEnoughIps(PhysicalMachine pm){
		Integer ips = ipsNeeded.get(pm.getLaboratory().getDatabaseId());		
		if(ips==null)ipsNeeded.put(pm.getLaboratory().getDatabaseId(), 1);
		else ipsNeeded.put(pm.getLaboratory().getDatabaseId(), ips+1);			
		if(ipsNeeded.get(pm.getLaboratory().getDatabaseId())>pm.getLaboratory().getAvailableIps().size())
			return false;
		else 
			return true;
	}
}
