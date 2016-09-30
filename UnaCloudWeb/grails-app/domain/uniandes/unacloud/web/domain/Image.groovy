package uniandes.unacloud.web.domain

import java.text.DecimalFormat

import uniandes.unacloud.web.domain.enums.ClusterEnum;
import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Entity to represent a virtual machine image which is represented by group of files to be executed by an hypervisor.
 * @author CesarF
 *
 */
class Image {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Image name
	 */
	String name
	
	/**
	 * indicates if the image is public or not
	 */
    boolean isPublic
	
	/**
	 * Size of files in bytes
	 */
	long fixedDiskSize
	
	/**
	 * username used to access the image
	 */
	String user
	
	/**
	 * password used to access the image
	 */
	String password
	
	/**
	 * Image operating system 
	 */
	OperatingSystem operatingSystem
	
	/**
	 * access protocol (SSH, RDP)
	 */
	String accessProtocol
	
	/**
	 * Main file path (File that can be executed by hypervisor in order to
	 * deploy the machine)
	 */
	String mainFile
	
	/**
	 * Indicates how many times the image files had been edited
	 */
	int imageVersion
	
	/**
	 * token to validate image message sent by client
	 */	
	String token
	
	/**
	 *Virtual machine state (UNAVAILABLE,DISABLE,AVAILABLE,REMOVING_CACHE,COPYING,IN_QUEUE) 
	 */
	ImageEnum state = ImageEnum.AVAILABLE;
	
	/**
	 * Owner 
	 */
	static belongsTo = [owner: User, repository: Repository]
	
	/**
	 * Last update date
	 */
	Date lastUpdate
	
	static constraints = {
    	mainFile (nullable: true)
		token nullable:true
		lastUpdate nullable:true
	}
	static mapping = {
		operatingSystem(lazy:false)
	}
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------	
	
	/**
	 * Returns the size of image in GB, MB, KB
	 * @return String size of machines: GB, MB, LB depends of files size
	 */
	def String getSize(){
		DecimalFormat df = new DecimalFormat("#.00");
		long diskSize = fixedDiskSize/1024;
		if(diskSize>1){
			if(diskSize/1024>1){				
				diskSize = diskSize/1024
				if(diskSize/1024>1)
				return  df.format(diskSize/1024)+" GB"
				else return  df.format(diskSize)+" MB"
			}				
			else return  df.format(diskSize)+" KB"		
		}
		else return df.format(fixedDiskSize)+" Bytes"
	}
	
	/**
	 * Changes the state of image to IN_QUEUE and clusters where it is embedded
	 * this method is used to avoid that image in cluster could not be deployed 
	 */
	def freeze(){
		this.putAt("state", ImageEnum.IN_QUEUE);
		def clusteres = Cluster.where{images{id==this.id;}}.findAll();
		for(cluster in clusteres){
			cluster.putAt("state", ClusterEnum.FREEZE);
		}
		this.save(flush:true)
	}
	
	/**
	 * Returns database id
	 * @return Long id
	 */
	def Long getDatabaseId(){
		return id;
	}
}	
