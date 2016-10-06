package uniandes.unacloud.web.services

import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Image;
import grails.transaction.Transactional

/**
 * This service contains all methods to manage Operating System: Operating System crud.
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
@Transactional
class OperatingSystemService {
	

    //-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Creates a new OS
	 * @param name OS name
	 * @param configurer OS configurer class
	 */
    def create(name, configurer){
		new OperatingSystem(name: name, configurer:configurer).save()		
	}
	
	/**
	 * Deletes the selected OS
	 * @param os OS to be deleted
	 */
	
	def delete(OperatingSystem os){
		if(PhysicalMachine.where{operatingSystem==os}.find()||Image.where{operatingSystem==os}.find())
			throw new Exception("Operating System is being used by some Physical Machine or Execution")
		os.delete()
	}
	
	/**
	 * Edits the given OS
	 * @param os OS to be edited
	 * @param name OS new name
	 * @param configurer OS new configurer class
	 */
	
	def setValues(OperatingSystem os, name, configurer){
		os.putAt("name", name)
		os.putAt("configurer", configurer)
	}
}
