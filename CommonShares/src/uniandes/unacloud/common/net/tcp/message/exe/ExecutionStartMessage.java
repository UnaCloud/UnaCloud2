package uniandes.unacloud.common.net.tcp.message.exe;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;

/**
 * Represents message to start an execution
 * @author CesarF
 *
 */
public class ExecutionStartMessage extends ImageOperationMessage implements Comparable<ExecutionStartMessage> {
	
	private static final long serialVersionUID = -5116988985857543662L;
	
	private static final String IMAGE = "image_id";
	
	private static final String VM_CORES = "vm_cores";
	
	private static final String VM_MEMORY = "vm_memory";
	
	private static final String EXE_TIME = "exe_time";

	private static final String VM_HOST_NAME = "vm_host_name";
	
	private static final String NET_INTERFACE = "net_interface";
	
	private static final String TRANSMISSION_TYPE = "transmission_type";
	
	private long imageId;
	
	private int vmCores;
	
	private int vmMemory;
	
	private Time exeTime;
	
	private String vmHostName;
	
	private String protocolType;
	
	private List<ImageNetInterfaceComponent> interfaces;
    
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param host
	 * @param executionId
	 * @param pmId
	 * @param imageId
	 * @param vmCores
	 * @param vmMemory
	 * @param exeTime
	 * @param vmHostName
	 * @param type
	 * @param interfaces
	 */
	public ExecutionStartMessage(String ip, int port, String host, long executionId, long pmId, long imageId, int vmCores, int vmMemory, 
			Time exeTime, String vmHostName, TransmissionProtocolEnum type, List<ImageNetInterfaceComponent> interfaces) {
		super(ip, port, host, ImageOperationMessage.VM_START, pmId, executionId);
		this.imageId = imageId;		
		this.vmCores = vmCores;
		this.vmMemory = vmMemory;		
		this.exeTime = exeTime;		
		this.vmHostName = vmHostName;		
		this.protocolType = type.name();
		this.interfaces = interfaces;		
	}
    
	public int getVmCores() {
		return vmCores;
	}
	
	public int getVmMemory() {
		return vmMemory;
	}
	
	public Time getExecutionTime() {
		return exeTime;
	}
	
	@Override
	public int compareTo(ExecutionStartMessage o) {
		return Long.compare(getExecutionId(), o.getExecutionId());
	}
		
	public String getHostname() {
		return vmHostName;
	}
	
	public long getImageId() {
		return imageId;
	}
	
	public TransmissionProtocolEnum getTransmissionType() {
		return TransmissionProtocolEnum.getEnum(protocolType);
	}
			
	public List<ImageNetInterfaceComponent> getInterfaces() {			
		return interfaces;
	}
	
	@Override
	public String toString() {
		return super.toString() + " executionTime: " + getExecutionTime();
	}
	
	@Override
	public void setMessageByStringJson(String format) {
		super.setMessageByStringJson(format);
		JSONObject json;
		json = new JSONObject(format);		
		this.imageId = json.getLong(IMAGE);		
		this.vmCores = json.getInt(VM_CORES);
		this.vmMemory = json.getInt(VM_MEMORY);		
		this.exeTime = (Time) json.get(EXE_TIME);		
		this.vmHostName = json.getString(VM_HOST_NAME);		
		this.protocolType = json.getString(TRANSMISSION_TYPE);
		JSONArray array = json.getJSONArray(NET_INTERFACE);
		this.interfaces = new ArrayList<ImageNetInterfaceComponent>();
		for (int i = 0; i < array.length(); i++)
			interfaces.add((ImageNetInterfaceComponent) array.get(i));
		
	}
	
	@Override
	protected JSONObject getJsonMessage() {
		JSONObject obj = super.getJsonMessage();
		obj.put(IMAGE, imageId);
		obj.put(VM_CORES, vmCores);
		obj.put(VM_MEMORY, vmMemory);
		obj.put(EXE_TIME, exeTime);
		obj.put(VM_HOST_NAME, vmHostName);
		obj.put(TRANSMISSION_TYPE, protocolType);
		JSONArray array = new JSONArray();
		if (interfaces != null)
			for (ImageNetInterfaceComponent interf: interfaces)
				array.put(interf);
		obj.put(NET_INTERFACE, array);
		return obj;
	}	
	
}
