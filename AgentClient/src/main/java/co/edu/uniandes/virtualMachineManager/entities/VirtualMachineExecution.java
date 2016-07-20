package co.edu.uniandes.virtualMachineManager.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.losandes.utils.Time;

import communication.messages.vmo.VirtualMachineStartMessage;
import communication.messages.vmo.VirtualNetInterfaceComponent;

/**
 * Represents a virtual machine execution entity
 * @author Clouder
 *
 */
public class VirtualMachineExecution implements Serializable{
	
	
	private static final long serialVersionUID = -6555573326780712925L;
	
	/**
	 * Virtual machine properties
	 */
	
	long id;
	int cores;
	int memory;
    Time executionTime;
    List<NetInterface> interfaces;
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

	public List<NetInterface> getInterfaces() {
		return interfaces;
	}
	
	public void setInterfaces(List<NetInterface> interfaces) {
		this.interfaces = interfaces;
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
	 * Temporal 
	 * @return first interface configured
	 */
	public NetInterface getMainInterface(){
		return interfaces.get(0);
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
		vme.setInterfaces(getInterfacesFromMessage(message.getInterfaces()));
		vme.setPersistent(message.isPersistent());
		return vme;
	}
	
	/**
	 * Transform  a list of interfaces from message to execution
	 * @param mInterfaces
	 * @return list of net interfacs
	 */
	private static List<NetInterface> getInterfacesFromMessage( List<VirtualNetInterfaceComponent> mInterfaces){
		List<NetInterface> interfaces = new ArrayList<NetInterface>();
		for(VirtualNetInterfaceComponent comp: mInterfaces){
			interfaces.add(new NetInterface(comp.name, comp.ip, comp.netMask));
		}
		return interfaces;
	}
}
