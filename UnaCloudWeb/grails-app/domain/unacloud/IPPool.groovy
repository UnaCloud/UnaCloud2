package unacloud

import unacloud.enums.NetworkQualityEnum;
import unacloud.share.enums.IPEnum;
import groovy.model.NestedValueModel;

/**
 * Entity to represent a group or pool of IP.
 * @author CesarF
 *
 */
class IPPool {
	
	//-----------------------------------------------------------------
	// Properties
	//-----------------------------------------------------------------
	
	/**
	 * Indicates if the IPs in this IPPool are public or private
	 */
	boolean privateNet 
	
	/**
	 * Common gateway to any IP in this pool
	 */
	String gateway
	
	/**
	 * common mask to any IP in this pool
	 */
	String mask
	
	/**
	 * list of IPs
	 */
	static hasMany = [ips: ExecutionIP]
	
	/**
	 * Laboratory which owns this ip pool
	 */
	
	static belongsTo = [laboratory: Laboratory]
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
    static constraints = {
    }
	
	
	/**
	 * Gets database object's id
	 * @return database id
	 */
	def long getDatabaseId(){
		return id;
	}
	
	
	/**
	 * Returns the quantity of available IP in pool
	 */
	def int getAvailableIpsQuantity(){
		return ips.findAll{it.state == IPEnum.AVAILABLE}.size()
	}
	
	/**
	 * Returns the quantity of used IP in pool
	 */
	def int getUsedIpsQuantity(){
		return ips.findAll{it.state == IPEnum.USED || it.state == IPEnum.RESERVED}.size()
	}
	
	/**
	 * Returns the quantity of non DISABLE IP in pool
	 */
	def int getIpsQuantity(){
		return ips.findAll{it.state != IPEnum.DISABLED}.size()
	}
	
	/**
	 * Return the first of IP in range
	 */
	def ExecutionIP first(){
		return ips.sort{it.ip}.getAt(0)
	}
	/**
	 * Return the last IP in range
	 */
	def ExecutionIP last(){
		return ips.sort{it.ip}.getAt(ips.size()-1)
	}
}
