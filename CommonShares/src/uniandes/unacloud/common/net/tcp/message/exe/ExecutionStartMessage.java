package uniandes.unacloud.common.net.tcp.message.exe;

import java.util.List;

import org.json.JSONObject;

import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;

/**
 * Represents message to start an execution
 * @author CesarF
 *
 */
public class ExecutionStartMessage extends ImageOperationMessage implements Comparable<ExecutionStartMessage> {
	
	private static final long serialVersionUID = -5116988985857543662L;
	
	public static final String IMAGE = "image_id";
	
	public static final String VM_CORES = "vm_cores";
	
	public static final String VM_MEMORY = "vm_memory";
	
	public static final String EXE_TIME = "exe_time";
	
	public static final String SNAP = "snap_route";
	
	public static final String PERSISTENT = "persistent";

	public static final String VM_HOST_NAME = "vm_host_name";
	
	public static final String NET_INTERFACE = "net_interface";
    
	public ExecutionStartMessage(String ip, int port, String host, long executionId, long pmId, long imageId, int vmCores, int vmMemory, Time exeTime, String snapshotRoute, boolean persistent, String vmHostName, List<ImageNetInterfaceComponent> interfaces) {
		super(ip, port, host, ImageOperationMessage.VM_START, pmId, executionId);		
		JSONObject tempMessage = this.getMessage();
		tempMessage.put(IMAGE, imageId);
		tempMessage.put(VM_CORES, vmCores);
		tempMessage.put(VM_MEMORY, vmMemory);
		tempMessage.put(EXE_TIME, exeTime);
		tempMessage.put(SNAP, snapshotRoute);
		tempMessage.put(PERSISTENT, persistent);
		tempMessage.put(VM_HOST_NAME, vmHostName);
		tempMessage.put(NET_INTERFACE, interfaces);
		this.setMessage(tempMessage);	
	}
    
	public int getVmCores() {
		return this.getMessage().getInt(VM_CORES);
	}
	
	public int getVmMemory() {
		return this.getMessage().getInt(VM_MEMORY);
	}
	
	public Time getExecutionTime() {
		return (Time) this.getMessage().get(EXE_TIME);
	}
	
	public boolean isPersistent() {
		return this.getMessage().getBoolean(PERSISTENT);
	}
	
	public String getSnapshotRoute() {
		return this.getMessage().getString(SNAP);
	}
	
	@Override
	public int compareTo(ExecutionStartMessage o) {
		return Long.compare(getExecutionId(), o.getExecutionId());
	}
		
	public String getHostname() {
		return this.getMessage().getString(VM_HOST_NAME);
	}
	
	public long getImageId() {
		return this.getMessage().getLong(IMAGE);
	}
	
	@Override
	public String toString() {
		return super.toString() + " executionTime: " + getExecutionTime();
	}
	
	public List<ImageNetInterfaceComponent> getInterfaces() {
		return (List<ImageNetInterfaceComponent>) this.getMessage().get(NET_INTERFACE);
	}
	
}
