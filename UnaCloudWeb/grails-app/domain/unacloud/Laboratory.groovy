package unacloud

import unacloud.enums.NetworkQualityEnum;

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
	 * list of physical machines belonging to this laboratory
	 */
	static hasMany = [physicalMachines: PhysicalMachine, ipPools: IPPool]
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