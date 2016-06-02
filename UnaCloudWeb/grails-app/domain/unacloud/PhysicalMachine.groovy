package unacloud

import com.losandes.enums.VirtualMachineExecutionStateEnum;

import unacloud.share.enums.PhysicalMachineStateEnum;
import unacloud.enums.MonitoringStatus;

/**
 * Entity to represent a host located in a computer room o laboratory
 * @author CesarF
 *
 */
class PhysicalMachine {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Physical machine name
	 */
    String name
	
	/**
	 * indicates if this machine is being used by an user
	 */
	boolean withUser = false
	
	/**
	 * number of core processors
	 */
	int cores
	
	/**
	 * number of physical core processors
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
	 * physical machine state (ON, OFF, DISABLED, PROCESSING)
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
		name unique:true
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
	 * @return laboratory where this Physical Machine belongs
	 */
	def Laboratory getLaboratory(){
		return laboratory;
	}
	
	/**
	 * Calculates the available resources in physical machine querying current resources used by executions
	 * @return an object with available resources in this host. Physical Cores, Cores, Ram, 
	 */
	def availableResources(){
		def usedResources = VirtualMachineExecution.executeQuery('select count(*) AS executions,sum(vme.hardwareProfile.ram) AS ram, sum(vme.hardwareProfile.cores) AS cores from VirtualMachineExecution as vme where vme.executionNode.id = :node_id and vme.status!=\'FINISHED\'',[node_id:this.id])		
		return [vms:usedResources[0][0]!=null?pCores-usedResources[0][0]:pCores,ram:usedResources[0][1]!=null?ram-usedResources[0][1]:ram,cores:usedResources[0][2]!=null?cores-usedResources[0][2]:cores]
	}
	
	/**
	 * Validates if physical machine has executions
	 * @return true in case there is at least one execution in machine, false in case not
	 */
	def withExecution(){
		return VirtualMachineExecution.where {executionNode==this&&status!=VirtualMachineExecutionStateEnum.FINISHED}.findAll().size()>0
	}
	
}
