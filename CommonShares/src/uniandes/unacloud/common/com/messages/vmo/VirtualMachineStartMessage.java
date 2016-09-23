package uniandes.unacloud.common.com.messages.vmo;

import java.util.List;

import uniandes.unacloud.common.com.messages.VirtualMachineOperationMessage;
import uniandes.unacloud.common.utils.Time;

/**
 * Represents message to start a virtual machine execution
 * @author CesarF
 *
 */
public class VirtualMachineStartMessage extends VirtualMachineOperationMessage implements Comparable<VirtualMachineStartMessage>{
	private static final long serialVersionUID = -5116988985857543662L;
	
	long virtualMachineImageId;
	int vmCores,vmMemory;
    Time executionTime;
    String snapshotRoute;
    boolean persistent;
    String hostname;
    List<VirtualNetInterfaceComponent>interfaces;
    
    public VirtualMachineStartMessage() {
		super(VM_START);
	}
	public int getVmCores() {
		return vmCores;
	}
	public int getVmMemory() {
		return vmMemory;
	}
	public Time getExecutionTime() {
		return executionTime;
	}
	public boolean isPersistent() {
		return persistent;
	}
	public String getSnapshotRoute() {
		return snapshotRoute;
	}
	@Override
	public int compareTo(VirtualMachineStartMessage o) {
		return Long.compare(getVirtualMachineExecutionId(),o.getVirtualMachineExecutionId());
	}
	public void setExecutionTime(Time executionTime) {
		this.executionTime = executionTime;
	}
	public String getHostname() {
		return hostname;
	}
	public void setVmCores(int vmCores) {
		this.vmCores = vmCores;
	}
	public void setVmMemory(int vmMemory) {
		this.vmMemory = vmMemory;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public void setVirtualMachineImageId(long virtualMachineImageId) {
		this.virtualMachineImageId = virtualMachineImageId;
	}
	public long getVirtualMachineImageId() {
		return virtualMachineImageId;
	}
	@Override
	public String toString() {
		return super.toString()+" executionTime: "+executionTime;
	}
	
	public List<VirtualNetInterfaceComponent> getInterfaces() {
		return interfaces;
	}
	
	public void setInterfaces(List<VirtualNetInterfaceComponent> interfaces) {
		this.interfaces = interfaces;
	}
}
