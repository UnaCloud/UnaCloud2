package unacloud

import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.MonitoringStatus
import unacloud.enums.VirtualMachineExecutionStateEnum;

class PhysicalMachine {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Physical machine name
	 */
    String name
	
	/**
	 * indicates if this machine is being used
	 */
	boolean withUser = false
	
	/**
	 * number of processors
	 */
	int cores
	
	/**
	 * number of physical processors
	 */
	int pCores
	
	/**
	 * quantity of RAM memory in MB
	 */
	int ram
	
	/**
	 * Indicates if this machine has high availability
	 */
	boolean highAvailability

	/**
	 * physical machine's IP address
	 */
	PhysicalIP ip
	
	/**
	 * physical machine's MAC address
	 */
	String mac
	
	/**
	 * physical machine state (ON, OFF, DISABLED)
	 */
	PhysicalMachineStateEnum state
	
	/**
	 * physical machine's OS
	 */
	OperatingSystem operatingSystem
	
	/**
	 * date when physical machine's agent last reported
	 */
	Date lastReport
	
	/**
	 * Free space in data directory: current virtual machine directory
	 */
	long dataSpace = 0;
	
	
	/**
	 * Laboratory to which the physical machine belongs
	 */
	Laboratory laboratory
	static belongsTo =  [laboratory:Laboratory]
	
	
	/**
	 * Monitoring System configured in physical machine
	 */
	MonitorSystem monitorSystem
	
	static constraints = {
		monitorSystem nullable:true
		lastReport nullable:true
		ip nullable:true
	}
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Gets database object's id
	 * @return database id
	 */
	def long getDatabaseId(){
		return id;
	}
	
	/**
	 * Gets laboratory 
	 * @return
	 */
	def Laboratory getLaboratory(){
		return laboratory;
	}
	
}
