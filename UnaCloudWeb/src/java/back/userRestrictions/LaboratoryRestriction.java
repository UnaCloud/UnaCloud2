package back.userRestrictions;

import java.util.List;

import unacloud2.PhysicalMachine;
import unacloud2.VirtualMachineExecution;

class LaboratoryRestriction implements UserRestrictionInterface{
	@Override
	public void applyRestriction(String value,List<VirtualMachineExecution> vms, List<PhysicalMachine> pms) {
		if(value!=null && !value.isEmpty()){
		String[] values= value.split(":");
		boolean isFromValues;
		for (int j=0;j<pms.size();j++){
			PhysicalMachine pm= pms.get(j);
			isFromValues= false;
			for (int i = 0; i < values.length && isFromValues== false; i++) {
				
				if (values[i].equals(pm.getLaboratory().getName())){
					isFromValues=true;
				}
			}
			if(!isFromValues){
				pms.remove(j);
				j--;
			}
		}
		}
	}
}