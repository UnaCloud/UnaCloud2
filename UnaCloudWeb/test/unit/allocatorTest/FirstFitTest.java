package allocatorTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import org.junit.Test;

import unacloud2.HardwareProfile;
import unacloud2.PhysicalMachine;
import unacloud2.VirtualMachineExecution;
import unacloud2.enums.PhysicalMachineStateEnum;
import back.pmallocators.AllocatorException;
import back.pmallocators.FirstFitAllocator;
import back.pmallocators.PhysicalMachineAllocationDescription;

public class FirstFitTest {

	@Test
	public void test1() {
		FirstFitAllocator ffa=new FirstFitAllocator();
		List<PhysicalMachine> pms=new ArrayList<>();
		List<VirtualMachineExecution> vms=new ArrayList<>();
		Map<Long, PhysicalMachineAllocationDescription> pmds=new TreeMap<Long, PhysicalMachineAllocationDescription>();
		Random r=new Random();
		for(int e=0;e<1000;e++){
			for(int i=r.nextInt(200);i>=0;i--){
				PhysicalMachineTest pm=new PhysicalMachineTest();
				pm.setCores(r.nextInt(3)+1);
				pm.setRam((r.nextInt(15)+1)*1024);
				pm.setId(i);
				pm.setState(PhysicalMachineStateEnum.ON);
				pm.setWithUser(r.nextInt(100)<25);
				pms.add(pm);
			}
			for(int i=r.nextInt(400);i>=0;i--){
				VirtualMachineExecution vme=new VirtualMachineExecution();
				HardwareProfile hp= new HardwareProfile();
				vme.setHardwareProfile(hp);
				vme.getHardwareProfile().setCores(r.nextInt(3)+1);
				vme.getHardwareProfile().setRam((r.nextInt(15)+1)*1024);
				vms.add(vme);
			}
			try {
				ffa.startAllocation(vms,pms,pmds);
			} catch (AllocatorException e1) {
				e1.printStackTrace();
			}
			pms.clear();vms.clear();pmds.clear();
		}
		
	}

}
