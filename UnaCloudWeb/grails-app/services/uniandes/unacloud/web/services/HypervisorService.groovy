package uniandes.unacloud.web.services

import uniandes.unacloud.web.domain.Hypervisor;
import grails.transaction.Transactional

/**
 * This service contains all methods to manage Hypervisor: crud hypervisor
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
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
	
	def create(name, hyperVersion, ext,fileExts) {
		new Hypervisor(name:name, hypervisorVersion: hyperVersion, mainExtension: ext, filesExtensions: fileExts).save()
    }
	
	/**
	 * Edits hypervisor's values
	 * @param hypervisor hypervisor to be edited
	 * @param name hypervisor's new name
	 */
	
	def setValues(Hypervisor hypervisor, name, hyperVersion, mainExt, filesExt){
		hypervisor.putAt("name", name)
		hypervisor.putAt("hypervisorVersion", hyperVersion)
		hypervisor.putAt("mainExtension", mainExt)
		hypervisor.putAt("filesExtensions", filesExt)
	}
	
	/**
	 * Deletes the selected hypervisor
	 * @param hypervisor hypervisor to be deleted
	 */
	
	def deleteHypervisor(Hypervisor hypervisor){
		hypervisor.delete()
	}
}
