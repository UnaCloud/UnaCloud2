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
	
	/**
	 * Calculates the available resources in physical machine querying current resources used by executions
	 * @return
	 */
	def availableResources(){
		def usedResources = VirtualMachineExecution.executeQuery('select count(*) AS executions,sum(vme.hardwareProfile.ram) AS ram, sum(vme.hardwareProfile.cores) AS cores from VirtualMachineExecution as vme where vme.executionNode.id = :node_id and vme.status!=\'FINISHED\'',[node_id:this.id])		
		return [vms:usedResources[0][0]!=null?pCores-usedResources[0][0]:pCores,ram:usedResources[0][1]!=null?ram-usedResources[0][1]:ram,cores:usedResources[0][2]!=null?cores-usedResources[0][2]:cores]
	}
	
}
