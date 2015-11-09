package unacloud

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
	
	def long numberOfMachines(){
		return physicalMachines.findAll{it.state!=PhysicalMachineStateEnum.DISABLED}.size()
	}
	
}