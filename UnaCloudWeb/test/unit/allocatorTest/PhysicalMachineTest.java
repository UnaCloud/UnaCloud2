package allocatorTest;

import unacloud2.PhysicalMachine;

public class PhysicalMachineTest extends PhysicalMachine{
	long id;
	public PhysicalMachineTest() {
	}
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public long getDatabaseId() {
		return id;
	}
}
