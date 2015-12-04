package unacloud

import java.util.List;

import unacloud.enums.IPEnum;
import unacloud.enums.NetworkQualityEnum;
import unacloud.enums.PhysicalMachineStateEnum;

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
	 * list of physical machines belonging to this laboratory
	 */
	static hasMany = [physicalMachines: PhysicalMachine, ipPools: IPPool]
	
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
	 * Gets the number of ips availables
	 * @return
	 */
	def long numberOfIps(){
		long number = 0;
		if(!ipPools)this.putAt("ipPools", [])
		for(IPPool pool: ipPools)number+=pool.getIpsQuantity()
		return number
	}
	/**
	 * Return the quantity of machines that are not disabled
	 * @return
	 */
	def long numberOfMachines(){
		return physicalMachines.findAll{it.state!=PhysicalMachineStateEnum.DISABLED}.size()
	}
	
	/**
	 * Returns cluster images sorted
	 * @return sorted images
	 */
	def List <PhysicalMachine> getOrderedMachines(){
		return physicalMachines.sort()
	}
	
	/**
	 * Return the list of available physical machines
	 * @param isHigh
	 * @return
	 */
	def List <PhysicalMachine> getAvailableMachines(isHigh){
		return physicalMachines.findAll{it.state==PhysicalMachineStateEnum.ON && it.highAvailability==isHigh}
	}
	
	/**
	 * Retrun the list of available ips in lab
	 * @param isHigh
	 * @return
	 */
	def List <ExecutionIP> getAvailableIps(){
		List <ExecutionIP> ips = new ArrayList<>()
		for(IPPool pool: ipPools)ips.addAll(pool.ips.findAll{it.state == IPEnum.AVAILABLE})
	}
}