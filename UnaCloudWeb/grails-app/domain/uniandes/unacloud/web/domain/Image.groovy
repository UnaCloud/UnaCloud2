package uniandes.unacloud.web.domain

import java.text.DecimalFormat

import uniandes.unacloud.web.domain.enums.ClusterEnum;
import uniandes.unacloud.common.utils.ByteUtils;
import uniandes.unacloud.share.enums.DeploymentStateEnum;
import uniandes.unacloud.share.enums.ImageEnum;

/**
 * Entity to represent an image which is represented by group of files to be executed by a platform.
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
	 * Main file path (File that can be executed by platform in order to
	 * deploy the machine)
	 */
	String mainFile
	
	/**
	 * Indicates how many times the image files had been edited
	 */
	int imageVersion = 1
	
	/**
	 * token to validate image message sent by client
	 */	
	String token = null
	
	/**
	 *Image state (UNAVAILABLE,DISABLE,AVAILABLE,REMOVING_CACHE,COPYING,IN_QUEUE) 
	 */
	ImageEnum state;
	
	/**
	 * Owner 
	 */
	static belongsTo = [owner: User, repository: Repository]
	
	/**
	 * Last update date
	 */
	Date lastUpdate
	
	/**
	 * Platform where image could be executed
	 */
	Platform platform
	
	static constraints = {
    	mainFile (nullable: true)
		token nullable:true
		lastUpdate nullable:true
		platform nullable: false
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
	def String getSize() {
		return ByteUtils.conversionUnitBytes(fixedDiskSize)
	}
	
	/**
	 * Changes the state of image to IN_QUEUE and clusters where it is embedded
	 * this method is used to avoid that image in cluster could not be deployed 
	 */
	def freeze() {
		this.putAt("state", ImageEnum.IN_QUEUE);
		def clusteres = Cluster.where{images{id == this.id;}}.findAll();
		for (cluster in clusteres)
			cluster.putAt("state", ClusterEnum.FREEZE);		
		this.save(flush:true)
	}
	
	/**
	 * Returns database id
	 * @return Long id
	 */
	def Long getDatabaseId() {
		return id;
	}

	/**
	 * returns platform
	 * @return platform
	 */
	def Platform getPlatform() {
		return platform;
	}
}	
