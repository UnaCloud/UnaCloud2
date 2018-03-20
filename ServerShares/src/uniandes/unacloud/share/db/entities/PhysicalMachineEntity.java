package uniandes.unacloud.share.db.entities;

import java.util.Date;

import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;


/**
 * Class to represent a Physical Machine entity 
 * @author CesarF
 *
 */
public class PhysicalMachineEntity {
	
	private Long id;
	
	private String ip;
	
	private Date lastReport;
	
	private PhysicalMachineStateEnum status;
	
	private String version;
	
	private Long freeSpace;
	
	private String host;
	
	private String logName;
	
	
	/**
	 * Empty constructor
	 */
	public PhysicalMachineEntity() {
		
	}
	
	/**
	 * Creates a physical machine with status only
	 * @param status of physical machine
	 * @param id of pm
	 */
	public PhysicalMachineEntity (Long id, PhysicalMachineStateEnum status) {
		this.id = id;
		this.status = status;
	}
	
	/**
	 * Creates a new pm with basic information
	 * @param id
	 * @param ip
	 * @param lastReport
	 * @param status
	 */
	public PhysicalMachineEntity (Long id, String ip, Date lastReport,
			PhysicalMachineStateEnum status, String host) {
		this.id = id;
		this.ip = ip;
		this.lastReport = lastReport;
		this.status = status;
		this.host = host;
	}
	
	
	/**
	 * Creates a new pm entity with all information
	 * @param id
	 * @param ip
	 * @param lastReport
	 * @param version
	 * @param dataSpace
	 * @param status
	 */
	public PhysicalMachineEntity (Long id, String ip, Date lastReport, String version, Long dataSpace,
			PhysicalMachineStateEnum status, String host, String logs) {
		super();
		this.id = id;
		this.ip = ip;
		this.lastReport = lastReport;
		this.status = status;
		this.version = version;
		this.freeSpace = dataSpace;
		this.host = host;
		this.logName = logs;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getLastReport() {
		return lastReport;
	}

	public void setLastReport(Date lastReport) {
		this.lastReport = lastReport;
	}

	public PhysicalMachineStateEnum getStatus() {
		return status;
	}

	public void setStatus(PhysicalMachineStateEnum status) {
		this.status = status;
	}
	
	public Long getFreeSpace() {
		return freeSpace;
	}
	
	public String getVersion() {
		return version;
	}
	
	public void setFreeSpace(long dataSpace) {
		this.freeSpace = dataSpace;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getLogName() {
		return logName;
	}
	
	public void setLogName(String logName) {
		this.logName = logName;
	}
}
