package unacloud.share.entities;

import java.util.Date;

import unacloud.share.enums.PhysicalMachineStateEnum;

/**
 * Class to represent an entity from database
 * Represents PhysicalMachine
 * @author Cesar
 *
 */
public class PhysicalMachineEntity {
	
	private Long id;
	private String ip;
	private Date lastReport;
	private PhysicalMachineStateEnum status;
	
	public PhysicalMachineEntity() {
	}
	
	public PhysicalMachineEntity(Long id, String ip, Date lastReport,
			PhysicalMachineStateEnum status) {
		super();
		this.id = id;
		this.ip = ip;
		this.lastReport = lastReport;
		this.status = status;
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

}
