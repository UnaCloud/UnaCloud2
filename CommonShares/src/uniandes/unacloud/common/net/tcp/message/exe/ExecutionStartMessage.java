package uniandes.unacloud.common.net.tcp.message.exe;

import java.io.Serializable;
import java.util.List;



import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.common.net.tcp.message.ImageOperationMessage;
import uniandes.unacloud.common.utils.Time;

/**
 * Represents message to start an execution
 * @author CesarF
 *
 */
public class ExecutionStartMessage extends ImageOperationMessage implements Comparable<ExecutionStartMessage>, Serializable {
	
	private static final long serialVersionUID = -5116988985857543662L;
	
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
		return "ExecutionStartMessage [imageId=" + imageId + ", vmCores="
				+ vmCores + ", vmMemory=" + vmMemory + ", exeTime=" + exeTime
				+ ", vmHostName=" + vmHostName + ", protocolType="
				+ protocolType + ", interfaces=" + interfaces + "]" + super.toString();
	}
	
		
}
