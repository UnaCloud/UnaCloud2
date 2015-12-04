package unacloud.pmallocators;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import unacloud.PhysicalMachine;
import unacloud.VirtualMachineExecution;

/**
 * Abstract class with main methods to allocate deployments. Validates enough resources in physical machine and enough IPs in lab
 * @author Cesar
 *
 */
public abstract class VirtualMachineAllocator{
	
	private TreeMap<Long, Integer> ipsNeeded;
	
	public VirtualMachineAllocator() {
		ipsNeeded = new TreeMap<Long, Integer>();
	}
	
	public synchronized void startAllocation(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,Map<Long,PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException{
		ipsNeeded = new TreeMap<Long, Integer>();
		allocateVirtualMachines(virtualMachineList, physicalMachines, physicalMachineDescriptions);
	}

	protected abstract void allocateVirtualMachines(List<VirtualMachineExecution> virtualMachineList,List<PhysicalMachine> physicalMachines,Map<Long,PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException;
	protected boolean fitVMonPM(VirtualMachineExecution vme,PhysicalMachine pm,PhysicalMachineAllocationDescription pmad){
		
		System.out.println("vm cores"+vme.getHardwareProfile().getCores()+"vm ram"+ vme.getHardwareProfile().getRam()+" pm cores"+pm.getCores()+"pm ram"+ pm.getRam());		
		if (pmad == null && vme.getHardwareProfile().getCores() <= pm.getCores() && vme.getHardwareProfile().getRam() <= pm.getRam())
		return isThereEnoughIps(pm);
		else if (pmad!= null && pmad.getCores() + vme.getHardwareProfile().getCores() <= pm.getCores()&& pmad.getRam() + vme.getHardwareProfile().getRam() <= pm.getRam() &&pmad.vms+1 < pm.getpCores())
		return isThereEnoughIps(pm);
		else return false;
	}
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
