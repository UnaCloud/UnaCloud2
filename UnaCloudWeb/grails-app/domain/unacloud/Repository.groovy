package unacloud

import com.losandes.utils.Constants;
import com.losandes.utils.UnaCloudConstants;

/**
 * Entity to represent a file system where virtual machine image files are located
 * @author CesarF
 *
 */
class Repository {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Repository name
	 */
	String name
	
	/**
	 * Repository capacity in GB
	 */
	int capacity
	
	/**
	 * Repository path
	 */
	String path
	
	/**
	 * Images stored in this repository
	 */
	static hasMany = [images: VirtualMachineImage]
	
    static constraints = {
		name unique:true
	}
	
	/**
	 * Validates if this repository is default one
	 * @return true is the repository is default, false is not
	 */	
	def boolean isDefault(){
		return this == Repository.findByName(UnaCloudConstants.MAIN_REPOSITORY);		
	}
}
