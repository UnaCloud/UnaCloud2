
package unacloudws.responses;

import java.util.Date;


public class VirtualMachineExecutionWS {
	
	protected int id;
	protected long imageId;
    protected String virtualMachineExecutionIP;
    protected VirtualMachineStatusEnum virtualMachineExecutionStatus;
    protected String virtualMachineExecutionStatusMessage;
    protected Date stopTime;
    protected String virtualMachineName;
	
    
    public VirtualMachineExecutionWS(long imageId,
			String virtualMachineExecutionIP,
			VirtualMachineStatusEnum virtualMachineExecutionStatus,
			String virtualMachineExecutionStatusMessage, int id, Date stopTime,
			String virtualMachineName) {
		this.imageId = imageId;
		this.virtualMachineExecutionIP = virtualMachineExecutionIP;
		this.virtualMachineExecutionStatus = virtualMachineExecutionStatus;
		this.virtualMachineExecutionStatusMessage = virtualMachineExecutionStatusMessage;
		this.stopTime = stopTime;
		this.id= id;
		this.virtualMachineName = virtualMachineName;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public long getImageId() {
		return imageId;
	}
	
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

	public String getVirtualMachineExecutionIP() {
		return virtualMachineExecutionIP;
	}


	public void setVirtualMachineExecutionIP(String virtualMachineExecutionIP) {
		this.virtualMachineExecutionIP = virtualMachineExecutionIP;
	}


	public VirtualMachineStatusEnum getVirtualMachineExecutionStatus() {
		return virtualMachineExecutionStatus;
	}


	public void setVirtualMachineExecutionStatus(
			VirtualMachineStatusEnum virtualMachineExecutionStatus) {
		this.virtualMachineExecutionStatus = virtualMachineExecutionStatus;
	}


	public String getVirtualMachineExecutionStatusMessage() {
		return virtualMachineExecutionStatusMessage;
	}


	public void setVirtualMachineExecutionStatusMessage(
			String virtualMachineExecutionStatusMessage) {
		this.virtualMachineExecutionStatusMessage = virtualMachineExecutionStatusMessage;
	}


	public Date getStopTime() {
		return stopTime;
	}


	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}


	public String getVirtualMachineName() {
		return virtualMachineName;
	}


	public void setVirtualMachineName(String virtualMachineName) {
		this.virtualMachineName = virtualMachineName;
	}

}
