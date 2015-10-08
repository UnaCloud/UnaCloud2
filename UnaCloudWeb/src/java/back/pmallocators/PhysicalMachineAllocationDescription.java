package back.pmallocators;

public class PhysicalMachineAllocationDescription {
	long nodeId;
	int cores;
	int ram;
	int vms;
	public PhysicalMachineAllocationDescription(long nodeId, int cores,int ram, int vms) {
		this.nodeId = nodeId;
		this.cores = cores;
		this.ram = ram;
		this.vms = vms;
	}
	public long getNodeId() {
		return nodeId;
	}
	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}
	public int getCores() {
		return cores;
	}
	public void setCores(int cores) {
		this.cores = cores;
	}
	public int getRam() {
		return ram;
	}
	public void setRam(int ram) {
		this.ram = ram;
	}
	public int getVms() {
		return vms;
	}
	public void setVms(int vms) {
		this.vms = vms;
	}
	public void addResources(int cores,int ram,int vms){
		this.vms+=vms;
		this.cores+=cores;
		this.ram+=ram;
		
	}
	@Override
	public String toString() {
		return "PhysicalMachineAllocationDescription [cores=" + cores
				+ ", ram=" + ram + ", vms=" + vms + "]";
	}
}
