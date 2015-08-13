package virtualMachineManager;

import hypervisorManager.ImageCopy;

import java.io.Serializable;

import com.losandes.utils.Time;

import communication.messages.vmo.VirtualMachineStartMessage;

public class VirtualMachineExecution implements Serializable{
	
	
	private static final long serialVersionUID = -6555573326780712925L;
	
	/**
	 * Virtual machine properties
	 */
	
	long id;
	int cores;
	int memory;
    Time executionTime;
    String ip,netMask;
    boolean persistent;
    String hostname;
    
    long shutdownTime;
    
    /**
     * Virtual machine image 
     */
    long imageId;
    ImageCopy image;
    
    /**
     * Class constructor
     */
    public VirtualMachineExecution() {
	}
    
    /**
     * Getters and setters
     */
    
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getCores() {
		return cores;
	}
	public void setCores(int cores) {
		this.cores = cores;
	}
	public int getMemory() {
		return memory;
	}
	public void setMemory(int memory) {
		this.memory = memory;
	}
	public Time getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(Time executionTime) {
		this.executionTime = executionTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getNetMask() {
		return netMask;
	}
	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}
	public boolean isPersistent() {
		return persistent;
	}
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public ImageCopy getImage() {
		return image;
	}
	public void setImage(ImageCopy image) {
		this.image = image;
	}
	public long getShutdownTime() {
		return shutdownTime;
	}
	public void setShutdownTime(long shutdownTime) {
		this.shutdownTime = shutdownTime;
	}
	public long getImageId() {
		return imageId;
	}
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
	
	/**
	 * Returns a virtual machine based on a start message
	 * @param message message with VM data
	 * @return VM object based on message
	 */
	public static VirtualMachineExecution getFromStartVirtualMachineMessage(VirtualMachineStartMessage message){
		VirtualMachineExecution vme=new VirtualMachineExecution();
		vme.setCores(message.getVmCores());
		vme.setMemory(message.getVmMemory());
		vme.setExecutionTime(message.getExecutionTime());
		vme.setHostname(message.getHostname());
		vme.setId(message.getVirtualMachineExecutionId());
		vme.setImageId(message.getVirtualMachineImageId());
		vme.setIp(message.getVmIP());
		vme.setNetMask(message.getVirtualMachineNetMask());
		vme.setPersistent(message.isPersistent());
		return vme;
	}
}
