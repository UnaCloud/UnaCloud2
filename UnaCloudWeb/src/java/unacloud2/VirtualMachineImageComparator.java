package unacloud2;
import java.util.Comparator;
import unacloud2.VirtualMachineImage;

public class VirtualMachineImageComparator implements Comparator<VirtualMachineImage>{
	@Override
	public int compare(VirtualMachineImage o1, VirtualMachineImage o2) {
		return (o1.getName()).compareTo(o2.getName());
	}
}
