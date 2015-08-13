package unacloud2

import unacloud2.enums.NetworkQualityEnum;

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
	 * IP pool available for deployed nodes.
	 */
	IPPool virtualMachinesIPs
	
	/**
	 * Indicates laboratory network quality
	 */
	NetworkQualityEnum networkQuality
	
	/**
	 * list of physical machines belonging to this laboratory
	 */
	static hasMany = [physicalMachines: PhysicalMachine]
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Return the physical machine list sorted by name
	 * @return physical machine ordered list
	 */
	ArrayList <PhysicalMachine> getOrderedMachines(){
		PhysicalMachineComparator c= new PhysicalMachineComparator()
		ArrayList <PhysicalMachine> array = new ArrayList(physicalMachines).sort(true,c)
		return array
	}
	/**
	 * Gets database object's id
	 * @return database id
	 */
	def long getDatabaseId(){
		return id;
	}
	
}