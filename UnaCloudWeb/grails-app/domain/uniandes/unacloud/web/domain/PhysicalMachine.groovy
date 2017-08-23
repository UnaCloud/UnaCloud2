package uniandes.unacloud.web.domain

import uniandes.unacloud.common.utils.ByteUtils;
import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;

/**
 * Entity to represent a host machine located in a computer room o laboratory
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
	 * quantity of core processors
	 */
	int cores
	
	/**
	 * quantity of physical core processors
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
	 * physical machine IP address
	 */
	PhysicalIP ip
	
	/**
	 * physical machine MAC address
	 */
	String mac
	
	/**
	 * physical machine state (ON, OFF, DISABLED, PROCESSING)
	 */
	PhysicalMachineStateEnum state
	
	/**
	 * physical machine OS
	 */
	OperatingSystem operatingSystem
	
	/**
	 * date when physical machine agent last reported
	 */
	Date lastReport
	
	/**
	 * Total space in bytes in data directory: current image directory
	 */
	long dataSpace = 0;
	
	/**
	 * Used space in bytes in data directory: current image directory
	 */
	long freeSpace = 0;
	
	/**
	 * Current agent version
	 */
	String agentVersion;
	
	/**
	 * Laboratory to which the physical machine belongs
	 */
	Laboratory laboratory	
	
	
	static belongsTo =  [laboratory:Laboratory]
	
	
	/**
	 * List of execution platforms
	 */
	static hasMany = [platforms: Platform]
		
	
	static constraints = {
		name unique:true
		lastReport nullable:true
		ip nullable:true
		agentVersion nullable:true
	}
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Gets database object id
	 * @return database id
	 */
	def long getDatabaseId() {
		return id;
	}
	
	/**
	 * Gets laboratory 
	 * @return laboratory where this Physical Machine belongs
	 */
	def Laboratory getLaboratory() {
		return laboratory;
	}
	
	/**
	 * Calculates the available resources in physical machine querying current resources used by executions
	 * @return an object with available resources in this host. Physical Cores, Cores, Ram, 
	 */
	def availableResources() {
		def usedResources = Execution.executeQuery('select count(*) AS executions, sum(vme.hardwareProfile.ram) AS ram, sum(vme.hardwareProfile.cores) AS cores from Execution as vme where vme.executionNode.id = :node_id and vme.state.state != \''+ExecutionStateEnum.FINISHED+"\'", [node_id:this.id])		
		return [vms:usedResources[0][0] != null ? pCores-usedResources[0][0]:pCores, ram:usedResources[0][1] != null ? ram-usedResources[0][1] : ram, cores:usedResources[0][2] != null ? cores-usedResources[0][2] : cores]
	}
	
	/**
	 * Validates if physical machine has executions
	 * @return true in case there is at least one execution in machine, false in case not
	 */
	def withExecution() {
		def exe = this
		return Execution.where {executionNode == exe && state.state != ExecutionStateEnum.FINISHED}.findAll().size() > 0
	}
	
	/**
	 * Responsible to return list of platform for physical machine
	 * @return list of platforms
	 */
	public Collection<Platform> getAllPlatforms() {
		return platforms;
	}
	
	/**
	 * Calculates used percentage disk in physical machine
	 * @return percentage of used disk
	 */
	def getUsedPercentage() {
		return dataSpace == 0 ? 100 : (dataSpace - freeSpace) * 100 / dataSpace;
	}
		
	/**
	 * Returns current agent version
	 * @return agent version
	 */
	def getCurrentAgentVersion() {
		return agentVersion == null ? "N/A" : agentVersion;
	}
	
	/**
	 * Returns the total disk size in physical machine
	 * @return total size disk
	 */
	def getTotalDiskSize () {
		return ByteUtils.conversionUnitBytes(dataSpace);
	}
	
	/**
	 * Returns the available disk size in physical machine
	 * @return available size disk
	 */
	def getAvailableDisk () {
		return ByteUtils.conversionUnitBytes(freeSpace);
	}
	
}
