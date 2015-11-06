package unacloud

import grails.transaction.Transactional

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
		if(PhysicalMachine.where{operatingSystem==os}.find()||VirtualMachineImage.where{operatingSystem==os}.find())
			throw new Exception("Operating System is being used by some Physical Machine or Virtual Machine")
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
