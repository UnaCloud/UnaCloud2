package unacloud2;

import java.util.Comparator;

public class VirtualMachineComparator implements Comparator<VirtualMachineExecution>{
	
	@Override
	public int compare(VirtualMachineExecution o1, VirtualMachineExecution o2) {
		return (o1.getName()).compareTo(o2.getName());
	}

}
