package back.userRestrictions;

import java.util.List;

import unacloud2.PhysicalMachine;
import unacloud2.VirtualMachineExecution;

public class MaxRamPerVMRestriction implements UserRestrictionInterface{

	@Override
	public void applyRestriction(String value,
			List<VirtualMachineExecution> vmes, List<PhysicalMachine> pms) throws UserRestrictionException {
		int ram= Integer.parseInt(value);
		for (VirtualMachineExecution  vme:vmes){
			if (vme.getHardwareProfile().getRam()>ram)
				throw new UserRestrictionException("Max RAM allowed:"+ram);
		}
		
	}

}
