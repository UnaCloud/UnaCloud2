package unacloud2;
import java.util.Comparator;

import unacloud2.PhysicalMachine;


public class PhysicalMachineComparator implements Comparator<PhysicalMachine>{

	@Override
	public int compare(PhysicalMachine o1, PhysicalMachine o2) {
		return (o1.getName()).compareTo(o2.getName());
	}

}
