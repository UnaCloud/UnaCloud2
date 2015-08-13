package unacloud2

import org.codehaus.groovy.grails.resolve.config.RepositoriesConfigurer;

import unacloud2.enums.PhysicalMachineStateEnum;
import enums.MonitoringStatus
import unacloudEnums.VirtualMachineExecutionStateEnum;

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
	boolean withUser
	
	/**
	 * number of processors
	 */
	int cores
	
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
	IP ip
	
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
	 * Laboratory to which the physical machine belongs
	 */
	Laboratory laboratory;
	/**
	 * Status of monitoring cpu process in agent
	 */
	MonitoringStatus monitorStatus;
	
	/**
	 * Status of monitoring energy process in agent
	 */
	MonitoringStatus monitorStatusEnergy;
	
	/**
	 * Free space in data directory: current virtual machine directory
	 */
	long dataSpace = 0;
	
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
	
}
