package uniandes.unacloud.share.db.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uniandes.unacloud.common.enums.ExecutionProcessEnum;

/**
 * Class to represent a Execution entity
 * @author CesarF
 *
 */
public class ExecutionEntity {
	
	private Long id;
	
	private int cores;
	
	private int ram;
	
	private PhysicalMachineEntity node;
	
	private ExecutionProcessEnum state;
	
	private String hostName;
	
	private List<NetInterfaceEntity> interfaces;
	
	private String message;
	
	private Date lastUpdate;
	
	private Date lastReport;
	
	private Long duration;
	
	/**
	 * Emptty constructor, all attributes have default value
	 */
	public ExecutionEntity() {
		
	}

	/**
	 * Creates a new Execution Entity with all attributes
	 * @param id
	 * @param cores
	 * @param ram
	 * @param duration
	 * @param node
	 * @param state
	 * @param hostName
	 * @param message
	 */
	public ExecutionEntity(Long id, int cores, int ram, 
			Long duration,
			PhysicalMachineEntity node,
			ExecutionProcessEnum state, String hostName, String message) {
		super();
		this.id = id;
		this.cores = cores;
		this.ram = ram;
		this.node = node;
		this.state = state;
		this.hostName = hostName;
		interfaces = new ArrayList<NetInterfaceEntity>();
		this.message = message;
		this.duration = duration;
	}
	
	/**
	 * Creates a new Execution Entity with all attributes
	 * @param id
	 * @param cores
	 * @param ram
	 * @param duration
	 * @param node
	 * @param hostName
	 * @param message
	 */
	public ExecutionEntity(Long id, int cores, int ram, 
			Long duration,
			PhysicalMachineEntity node, String hostName, String message) {
		super();
		this.id = id;
		this.cores = cores;
		this.ram = ram;
		this.node = node;
		this.hostName = hostName;
		interfaces = new ArrayList<NetInterfaceEntity>();
		this.message = message;
		this.duration = duration;
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public Date getLastReport() {
		return lastReport;
	}
	
	public void setLastReport(Date lastReport) {
		this.lastReport = lastReport;
	}

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
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
	
	public PhysicalMachineEntity getNode() {
		return node;
	}

	public void setNode(PhysicalMachineEntity node) {
		this.node = node;
	}

	public ExecutionProcessEnum getState() {
		return state;
	}

	public void setState(ExecutionProcessEnum state) {
		this.state = state;
	}

	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	public List<NetInterfaceEntity> getInterfaces() {
		return interfaces;
	}
	
	public void setInterfaces(List<NetInterfaceEntity> interfaces) {
		this.interfaces = interfaces;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Returns time execution in hours
	 * @return execution time in hours
	 */
	public Long getTimeInHours() {
		return (duration / 1000 / 60 / 60);
	}
	
	/**
	 * Returns time execution in milliseconds
	 * @return execution time in milliseconds
	 */
	public Long getDuration() {
		return duration;
	}
}
