package unacloud2

class HypervisorService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Creates a new hypervisor in database
	 * @param name hypervisor's name 
	 * @param hyperVersion hypervisor's version
	 */
	
	def addHypervisor(name, hyperVersion) {
		def h= new Hypervisor(name:name, hypervisorVersion: hyperVersion)
		h.save()
    }
	
	/**
	 * Deletes the selected hypervisor
	 * @param hypervisor hypervisor to be deleted
	 */
	
	def deleteHypervisor(Hypervisor hypervisor){
		hypervisor.delete()
	}
	
	/**
	 * Edit hypervisor's values
	 * @param hypervisor hypervisor to be edited
	 * @param name hypervisor's new name
	 */
	
	def setValues(Hypervisor hypervisor, name){
		hypervisor.putAt("name", name)
		
	}
}
