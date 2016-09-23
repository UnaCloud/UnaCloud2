package uniandes.unacloud.web.domain

import java.util.List;

import uniandes.unacloud.web.domain.enums.NetworkQualityEnum;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;

/**
 * Entity to represent a group of hosts or physical machines located in the same place, exactly how works university computer rooms.
 * @author CesarF
 *
 */
class Laboratory {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Laboratory name
	 */
    String name
	
	/**
	 * indicates if the laboratory contains high availability machines
	 */
	boolean highAvailability
		
	/**
	 * Indicates laboratory network quality
	 */
	NetworkQualityEnum networkQuality
	
	/**
	 * State of laboratory
	 */
	boolean enable = true;
	
	/**
	 * list of physical machines belong to this laboratory
	 * list of IP pools belong to this laboratory
	 */
	static hasMany = [physicalMachines: PhysicalMachine, ipPools: IPPool]
	
	
	/**
	 * The name of laboratory must be unique
	 */
	static constraints = {
		name unique: true
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
	 * Gets the number of available IP's 
	 * @return quantity of IP's
	 */
	def long numberOfIps(){
		long number = 0;
		if(!ipPools)this.putAt("ipPools", [])
		for(IPPool pool: ipPools)number+=pool.getIpsQuantity()
		return number
	}
	/**
	 * Returns the quantity of machines that are not disabled
	 * @return Long quantity of machines that are not DISABLED
	 */
	def long numberOfMachines(){
		return physicalMachines.findAll{it.state!=PhysicalMachineStateEnum.DISABLED}.size()
	}
	
	/**
	 * Returns cluster images sorted
	 * @return list of sorted images by id
	 */
	def List <PhysicalMachine> getOrderedMachines(){
		return physicalMachines.sort()
	}
	
	/**
	 * Returns the list of available physical machines (state ON)
	 * @param isHigh, query by high availability or not 
	 * @return list of Physical Machines
	 */
	def List <PhysicalMachine> getAvailableMachines(isHigh){
		return physicalMachines.findAll{it.state==PhysicalMachineStateEnum.ON && it.highAvailability==isHigh}.sort()
	}
	
	/**
	 * Returns the list of available execution IP in laboratory to be assigned
	 * @return list of Execution IP
	 */
	def List <ExecutionIP> getAvailableIps(){
		List <ExecutionIP> ips = new ArrayList<>()
		for(IPPool pool: ipPools)ips.addAll(pool.ips.findAll{it.state == IPEnum.AVAILABLE}.sort())
		return ips
	}
}