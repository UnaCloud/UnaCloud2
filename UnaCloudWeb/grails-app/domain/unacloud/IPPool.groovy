package unacloud

import unacloud.enums.IPEnum;
import unacloud.enums.NetworkQualityEnum;
import groovy.model.NestedValueModel;

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
	 * Laboratory which owns ippool
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
	 * Method to return the ips segment quantity
	 */
	def int getAvailableIpsQuantity(){
		return ips.findAll{it.state == IPEnum.AVAILABLE}.size()
	}
	
	/**
	 * Method to return the ips segment quantity
	 */
	def int getIpsQuantity(){
		return ips.findAll{it.state != IPEnum.DISABLE}.size()
	}
	
}
