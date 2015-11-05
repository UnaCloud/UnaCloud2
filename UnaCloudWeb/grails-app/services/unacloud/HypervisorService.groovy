package unacloud

import grails.transaction.Transactional

@Transactional
class HypervisorService {

   //-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Creates a new hypervisor in database
	 * @param name hypervisor's name 
	 * @param hyperVersion hypervisor's version
	 */
	
	def create(name, hyperVersion) {
		new Hypervisor(name:name, hypervisorVersion: hyperVersion).save()
    }
}
