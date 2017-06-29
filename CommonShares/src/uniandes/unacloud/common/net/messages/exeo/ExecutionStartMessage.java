package uniandes.unacloud.common.net.messages.exeo;

import java.util.List;

import uniandes.unacloud.common.net.messages.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;

/**
 * Represents message to start an execution
 * @author CesarF
 *
 */
public class ExecutionStartMessage extends ImageOperationMessage implements Comparable<ExecutionStartMessage> {
	
	private static final long serialVersionUID = -5116988985857543662L;
	
	private long imageId;
	
	private int vmCores, vmMemory;
	
	private Time executionTime;
	
	private String snapshotRoute;
	
    boolean persistent;
    
    private String hostname;
    
    private List<ImageNetInterfaceComponent> interfaces;
    
    public ExecutionStartMessage() {
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
	public int compareTo(ExecutionStartMessage o) {
		return Long.compare(getExecutionId(), o.getExecutionId());
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
	
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
	
	public long getImageId() {
		return imageId;
	}
	
	@Override
	public String toString() {
		return super.toString() + " executionTime: " + executionTime;
	}
	
	public List<ImageNetInterfaceComponent> getInterfaces() {
		return interfaces;
	}
	
	public void setInterfaces(List<ImageNetInterfaceComponent> interfaces) {
		this.interfaces = interfaces;
	}
}
