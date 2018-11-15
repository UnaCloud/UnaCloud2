package uniandes.unacloud.web.pmallocators;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uniandes.unacloud.web.domain.DeployedImage;
import uniandes.unacloud.web.domain.Image;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;
import uniandes.unacloud.web.domain.Platform;

/**
 * Abstract class with main methods to allocate deployments. Validates enough resources in physical machine and enough IPs in lab
 * The purpose of this class is to be extended to code allocator algorithms 
 * @author Clouder and CesarF
 *
 */
public abstract class ExecutionAllocator{
	
	/**
	 * Required IPs for deployment
	 */
	private TreeMap<Long, Integer> ipsNeeded;
	
	/**
	 * Creates an execution allocator
	 */
	public ExecutionAllocator() {
		ipsNeeded = new TreeMap<Long, Integer>();
	}
	
	/**
	 * Start the allocation process
	 * @param executionList
	 * @param physicalMachines
	 * @param physicalMachineDescriptions
	 * @throws AllocatorException
	 */
	public synchronized void startAllocation(List<Execution> executionList, List<PhysicalMachine> physicalMachines, Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException{
		ipsNeeded = new TreeMap<Long, Integer>();
		allocateExecutions(executionList, physicalMachines, physicalMachineDescriptions);
	}

	/**
	 * Method to match physical machines with executions.
	 * @param executionList
	 * @param physicalMachines
	 * @param physicalMachineDescriptions
	 * @throws AllocatorException
	 */
	protected abstract void allocateExecutions(List<Execution> executionList, List<PhysicalMachine> physicalMachines, Map<Long, PhysicalMachineAllocationDescription> physicalMachineDescriptions)throws AllocatorException;

	SimpleDateFormat sf= new SimpleDateFormat("hh:mm:ss");
	/**
	 * validates if an execution fits with resources of a physical machine
	 * @param vme
	 * @param pm
	 * @param pmad
	 * @return true if there is enough resources in physical machine to assign execution 
	 */
	protected boolean fitEXonPM(Execution vme, PhysicalMachine pm, PhysicalMachineAllocationDescription pmad){

		System.out.println(sf.format(new Date())+"\t Requires: " + ( (Image) ( (DeployedImage) vme.getDeployedImage() ).getImage()).getPlatform().getName());
		if (!isSupportedPlatform( ( (Image) ( (DeployedImage ) vme.getDeployedImage() ).getImage() ).getPlatform(), pm))
			return false;
		System.out.println("Required: exe cores" + vme.getHardwareProfile().getCores() + " exe ram" +  vme.getHardwareProfile().getRam() + " pm cores" + pm.getCores() + "pm ram" + pm.getRam());	
		System.out.println("Used "+pmad);
		if (pmad == null && vme.getHardwareProfile().getCores() <= pm.getCores() && vme.getHardwareProfile().getRam() <= pm.getRam())
			return isThereEnoughIps(pm);
		else if (pmad != null && pmad.getCores() + vme.getHardwareProfile().getCores() <= pm.getCores() && pmad.getRam() + vme.getHardwareProfile().getRam() <= pm.getRam() && pmad.getVms() + 1 <= pm.getpCores())
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
		if (ips == null)
			ipsNeeded.put(pm.getLaboratory().getDatabaseId(), 1);
		else ipsNeeded.put(pm.getLaboratory().getDatabaseId(), ips + 1);
		System.out.println(sf.format(new Date())+"\tA "+(ipsNeeded.get(pm.getLaboratory().getDatabaseId())));
		System.out.println("B "+( pm.getLaboratory().getAvailableIps().size()));
		System.out.println("Check ips "+(ipsNeeded.get(pm.getLaboratory().getDatabaseId()) > pm.getLaboratory().getAvailableIps().size()));
		if (ipsNeeded.get(pm.getLaboratory().getDatabaseId()) > pm.getLaboratory().getAvailableIps().size())
			return false;
		else 
			return true;
	}
	/**
	 * Responsible to validate if a platform is supported in physical machine
	 * @param platform
	 * @param pm
	 * @return true if platform is supported else false
	 */
	private boolean isSupportedPlatform(Platform platform, PhysicalMachine pm){
		for (Platform plat : pm.getAllPlatforms())
			if (plat.getDatabaseId() == platform.getDatabaseId())
				return true;
		
		return false;
	}
}
