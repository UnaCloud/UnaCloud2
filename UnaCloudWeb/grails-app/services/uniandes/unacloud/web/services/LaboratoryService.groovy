package uniandes.unacloud.web.services

import org.springframework.aop.ThrowsAdvice;

import uniandes.unacloud.web.domain.enums.NetworkQualityEnum;
import uniandes.unacloud.share.enums.ExecutionStateEnum;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.web.queue.QueueTaskerControl;
import uniandes.unacloud.web.utils.groovy.Utils;
import uniandes.unacloud.web.domain.ExecutionIP;
import uniandes.unacloud.web.domain.HardwareProfile;
import uniandes.unacloud.web.domain.IPPool;
import uniandes.unacloud.web.domain.Laboratory;
import uniandes.unacloud.web.domain.NetInterface;
import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.PhysicalIP;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;
import uniandes.unacloud.web.domain.Platform;
import uniandes.unacloud.common.enums.TaskEnum;
import uniandes.unacloud.common.utils.Ip4Validator
import grails.transaction.Transactional

/**
 * This service contains all methods to manage Laboratory: return a list of hardware profiles and query by name filter.
 * This class connects with database using hibernate
 * @author CesarF
 *
 */
@Transactional
class LaboratoryService {

	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
    /**
	 * Returns the lab name list
	 * @return lab name list
	 */
	def getLabsNames() {
		return Laboratory.executeQuery("select name from Laboratory")
	}
	
	/**
	 * Returns all labs searched by names array
	 * @param names list of lab names
	 * @return list of Hardware Profiles
	 */
	def getLabsByName(String[] names) {
		if (names == null) 
			return Laboratory.all
		return Laboratory.where{name in names && enable == true}.findAll()
	}
	
	/**
	 * Creates a new laboratory
	 * @param name Laboratory name
	 * @param highAvailability indicates if it's a high availability laboratory
	 * @param netConfig network configuration
	 * @param virtual indicates if the laboratory uses private IPs
	 * @param netGateway laboratory network's gateway
	 * @param netMask laboratory network's mask
	 */	
	def createLab(name, highAvailability, NetworkQualityEnum netConfig, privateNet, netGateway, netMask, ipInit, ipEnd) {
		ArrayList<String> ips = Utils.createRange(ipInit, ipEnd)
		if (ips.size() == 0) 
			throw new Exception("IP range invalid")
		//TODO save lab after validate each IP
		Laboratory lab = new Laboratory (name: name, highAvailability: highAvailability, networkQuality: netConfig, ipPools: [], physicalMachines: []).save();
		def ipPool = new IPPool(privateNet :privateNet, gateway: netGateway, mask: netMask, laboratory: lab).save()		
		for (String ipFind: ips)
			new ExecutionIP(ip: ipFind, ipPool: ipPool).save()			
	}
	
	
	
	/**
	 * Changes the status of a laboratory
	 * @param lab laboratory to be edited
	 */
	def setStatus(Laboratory lab) {
		if (lab.enable) 
			lab.putAt("enable", false)
		else 
			lab.putAt("enable", true)		
	}
	
	/**
	 * Deletes a laboratory, validates that lab does not have machines
	 * @param lab laboratory to be deleted
	 */
	def delete(Laboratory lab) {
		if (lab.physicalMachines.size() > 0) 
			throw new Exception("Laboratory is not empty, you must delete all physical machines in lab first.")
		lab.delete()
	}
	/**
	 * Modifies basic characteristics in lab
	 * @param lab Laboratory to be modified
	 * @param name new name of laboratory
	 * @param netConfig Network configuration
	 * @param highAvailability if lab is high availability
	 */
	def setValues(Laboratory lab, String name, NetworkQualityEnum netConfig, highAvailability) {
		lab.putAt("name", name)
		lab.putAt("networkQuality", netConfig)
		if (lab.highAvailability != highAvailability) {
			for(PhysicalMachine machine in lab.physicalMachines)
				machine.putAt("highAvailability", highAvailability)
			lab.putAt("highAvailability", highAvailability)
		}		
	}	
	
	/**
	 * Calculates the quantity of available deployments by hardware profiles
	 * @param lab where will be calculated the available resources
	 * @param hwProfiles profiles to calculate available deployments
	 * @param highAvailability if high availability resources should be calculated
	 * @param platform to filter resources
	 */
	def calculateDeploys(Laboratory lab, def hwProfiles, boolean highAvailability, platform) {
		TreeMap<String, Integer> results = new TreeMap<String,Integer>();	
		def availableIps = lab.getAvailableIps()		
		lab.physicalMachines.findAll{it.state == PhysicalMachineStateEnum.ON && it.platforms.find{it.id == platform.id} != null && it.highAvailability == highAvailability ? 1 : 0}.each{	
			def pmId = it.id;
			//How much resources in host are available in this moment			
			def availableResources = it.availableResources()			
			for (HardwareProfile hwd in hwProfiles) {
				def quantityRam = Math.floor(availableResources.ram / hwd.ram)
				def quantityCores = Math.floor(availableResources.cores / hwd.cores)
				def quantity = (quantityRam > quantityCores ? quantityCores : quantityRam)
				def finalQuantity = quantity > availableResources.vms ? availableResources.vms : quantity
				if (finalQuantity < 0) 
						finalQuantity = 0				
				if (results.get(hwd.name) == null)					
					results.put(hwd.name, finalQuantity)
				else
					results.put(hwd.name,results.get(hwd.name) + finalQuantity);
			}			
		}
		for (HardwareProfile hwd in hwProfiles) {
			if (results.get(hwd.name) == null)
				results.put(hwd.name, 0);
			if (availableIps.size() < results.get(hwd.name))
				results.put(hwd.name, availableIps.size());
		}		
		return results
	}
	
	
}
