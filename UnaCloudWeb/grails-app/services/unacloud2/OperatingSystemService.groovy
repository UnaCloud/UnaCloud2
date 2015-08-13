package unacloud2

class OperatingSystemService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Creates a new OS
	 * @param name OS name
	 * @param configurer OS configurer class
	 */
    def addOS(name, configurer){
		def o= new OperatingSystem(name: name , configurer:configurer )
		o.save()
		
	}
	
	/**
	 * Deletes the selected OS
	 * @param os OS to be deleted
	 */
	
	def deleteOS(OperatingSystem os){
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
