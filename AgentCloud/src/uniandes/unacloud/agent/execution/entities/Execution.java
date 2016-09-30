package uniandes.unacloud.agent.execution.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import uniandes.unacloud.common.com.messages.vmo.VirtualMachineStartMessage;
import uniandes.unacloud.common.com.messages.vmo.VirtualNetInterfaceComponent;
import uniandes.unacloud.common.utils.Time;

/**
 * Represents a virtual machine execution entity
 * @author Clouder
 *
 */
public class Execution implements Serializable{
	
	
	private static final long serialVersionUID = -6555573326780712925L;
	
	/**
	 * execution id
	 */	
	private long id;
	/**
	 * Execution cores
	 */
	private int cores;
	/**
	 * Execution memory in MB
	 */
	private int memory;
	/**
	 * Execution time
	 */
    private Time executionTime;
    /**
     * Execution net interfaces list
     */
    private List<NetInterface> interfaces;
    /**
     * Identifies if execution should persist
     */
    private boolean persistent;
    
    /**
     * Execution hostname
     */
    private String hostname;
    
    /**
     * Time to shutdown
     */
    private long shutdownTime;
    
    /**
     * Virtual machine image id
     */
    private long imageId;
    
    /**
     * Image copy to be executed
     */
    private ImageCopy image;
    
    /**
     * Class constructor
     */
    public Execution() {	}
    
  
    /**
     * Gets execution ID
     * @return
     */
	public long getId() {
		return id;
	}
	
	/**
	 *Sets execution ID
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}
	
	/**
	 * Gets cores quantity
	 * @return
	 */
	public int getCores() {
		return cores;
	}
	/**
	 * update cores quantity
	 * @param cores
	 */
	public void setCores(int cores) {
		this.cores = cores;
	}
	
	/**
	 * Gets execution memory en MB
	 * @return
	 */
	public int getMemory() {
		return memory;
	}
	
	/**
	 * Sets memory in MB
	 * @param memory
	 */
	public void setMemory(int memory) {
		this.memory = memory;
	}
	
	/**
	 * Gets execution time
	 * @return
	 */
	public Time getExecutionTime() {
		return executionTime;
	}
	
	/**
	 * Sets execution time 
	 * @param executionTime
	 */
	public void setExecutionTime(Time executionTime) {
		this.executionTime = executionTime;
	}

	/**
	 * Gets net interface list
	 * @return
	 */
	public List<NetInterface> getInterfaces() {
		return interfaces;
	}
	
	/**
	 * Update net interface list
	 * @param interfaces
	 */
	public void setInterfaces(List<NetInterface> interfaces) {
		this.interfaces = interfaces;
	}
	
	/**
	 * Response if execution is persistent
	 * @return boolean
	 */
	public boolean isPersistent() {
		return persistent;
	}
	
	/**
	 * Update if image is persistent
	 * @param persistent
	 */
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}
	
	/**
	 * Gets hostname
	 * @return hostname
	 */
	public String getHostname() {
		return hostname;
	}
	
	/**
	 * Update hostname
	 * @param hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	/**
	 * Gets Image copy
	 * @return image copy
	 */
	public ImageCopy getImage() {
		return image;
	}
	
	/**
	 * Sets Image Copy
	 * @param image
	 */
	public void setImage(ImageCopy image) {
		this.image = image;
	}
	
	/**
	 * Gets shutdown time in milliseconds
	 * @return milliseconds
	 */
	public long getShutdownTime() {
		return shutdownTime;
	}
	
	/**
	 * Update shutdown time in milliseconds
	 * @param shutdownTime
	 */
	public void setShutdownTime(long shutdownTime) {
		this.shutdownTime = shutdownTime;
	}
	
	/**
	 * Gets image id
	 * @return
	 */
	public long getImageId() {
		return imageId;
	}
	
	/**
	 * Update image id
	 * @param imageId
	 */
	public void setImageId(long imageId) {
		this.imageId = imageId;
	}
	
	/**
	 * TODO: Temporal 
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
	public static Execution getFromStartVirtualMachineMessage(VirtualMachineStartMessage message){
		Execution vme=new Execution();
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
	 * Transforms  a list of interfaces from message to execution
	 * @param mInterfaces
	 * @return list of net interfaces
	 */
	private static List<NetInterface> getInterfacesFromMessage( List<VirtualNetInterfaceComponent> mInterfaces){
		List<NetInterface> interfaces = new ArrayList<NetInterface>();
		for(VirtualNetInterfaceComponent comp: mInterfaces){
			interfaces.add(new NetInterface(comp.name, comp.ip, comp.netMask));
		}
		return interfaces;
	}
}
