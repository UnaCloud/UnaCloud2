package uniandes.unacloud.web.services

import org.springframework.aop.ThrowsAdvice;

import uniandes.unacloud.web.domain.enums.NetworkQualityEnum;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.web.queue.QueueTaskerControl;
import uniandes.unacloud.web.domain.ExecutionIP;
import uniandes.unacloud.web.domain.HardwareProfile;
import uniandes.unacloud.web.domain.IPPool;
import uniandes.unacloud.web.domain.Laboratory;
import uniandes.unacloud.web.domain.OperatingSystem;
import uniandes.unacloud.web.domain.PhysicalIP;
import uniandes.unacloud.web.domain.PhysicalMachine;
import uniandes.unacloud.web.domain.Execution;
import uniandes.unacloud.web.domain.Platform;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
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
	def getLabsNames(){
		return Laboratory.executeQuery("select name from Laboratory")
	}
	
	/**
	 * Returns all labs searched by names array
	 * @param names list of lab names
	 * @return list of Hardware Profiles
	 */
	def getLabsByName(String[] names){
		if(names==null)return Laboratory.all
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
	
	def createLab(name, highAvailability, NetworkQualityEnum netConfig, privateNet, netGateway, netMask, ipInit, ipEnd){
		ArrayList<String> ips = createRange(ipInit,ipEnd)
		Laboratory lab = new Laboratory (name: name, highAvailability: highAvailability,networkQuality: netConfig, ipPools:[],physicalMachines:[]).save();
		def ipPool=new IPPool(privateNet:privateNet,gateway: netGateway, mask: netMask, laboratory: lab).save()		
		for(String ipFind: ips){
			new ExecutionIP(ip:ipFind,ipPool:ipPool).save()
		}
	}
	
	/**
	 * Adds a new machine to a given laboratory
	 * @param ip physical machine's IP
	 * @param name physical machine's name
	 * @param cores physical machine's number of processors
	 * @param ram physical machine's RAM memory
	 * @param disk physical machine's available disk space
	 * @param osId physical machine's operating system
	 * @param mac physical machine's MAC address
	 * @param lab Laboratory
	 */
	
	def addMachine(ip, name, cores, pCores, ram, osId, mac, Laboratory lab, plats) {
		def physicalMachine = new PhysicalMachine(name:name, cores:cores, pCores:pCores, ram: ram, highAvailability:(lab.highAvailability),
			mac:mac, state: PhysicalMachineStateEnum.OFF,operatingSystem: OperatingSystem.get(osId),laboratory:lab, ip:new PhysicalIP(ip:ip), platforms: [])
		if(plats.getClass().equals(String))
			physicalMachine.platforms.add(Platform.get(plats))
		else{
			for(platId in plats){
				physicalMachine.platforms.add(Platform.get(platId))
			}
		}
		physicalMachine.save(failOnError:true)	
	}
	
	/**
	 * Sets values in a host machine
	 * @param ip new ip for host
	 * @param name new name
	 * @param cores new logical cores quantity
	 * @param pCores new physical cores quantity
	 * @param ram new memory in host
	 * @param osId new operating system id
	 * @param mac new MAC address
	 * @param host new name in network
	 * @return
	 */
	def editMachine(ip, name, cores, pCores, ram, osId, mac, PhysicalMachine host, plats){
		if(!host.ip.ip.equals(ip)){
			host.ip.setIp(ip)
		}
		host.setName(name)
		host.setMac(mac)
		host.setCores(Integer.parseInt(cores))
		host.setpCores(Integer.parseInt(pCores))
		host.setRam(Integer.parseInt(ram))
		host.setOperatingSystem(OperatingSystem.get(osId))
		Set platforms = []
		if(plats.getClass().equals(String))
			platforms.add(Platform.get(plats))
		else{
			for(platId in plats){
				platforms.add(Platform.get(platId))
			}
		}
		host.platforms = platforms
		host.save(failOnError:true)
	}
	
	/**
	 * Changes the status of a laboratory
	 * @param lab laboratory to be edited
	 */
	def setStatus(Laboratory lab){
		if(lab.enable)lab.putAt("enable", false)
		else lab.putAt("enable", true)		
	}
	
	/**
	 * Deletes a laboratory, validates that lab does not have machines
	 * @param lab laboratory to be deleted
	 */
	def delete(Laboratory lab){
		if(lab.physicalMachines.size()>0)throw new Exception("Laboratory is not empty, you must delete all physical machines in lab first.")
		lab.delete()
	}
	/**
	 * Modifies basic characteristics in lab
	 * @param lab Laboratory to be modified
	 * @param name new name of laboratory
	 * @param netConfig Network configuration
	 * @param highAvailability if lab is high availability
	 */
	def setValues(Laboratory lab, String name, NetworkQualityEnum netConfig, highAvailability){
		lab.putAt("name", name)
		lab.putAt("networkQuality", netConfig)
		if(lab.highAvailability!=highAvailability){
			for(PhysicalMachine machine in lab.physicalMachines)
				machine.putAt("highAvailability", highAvailability)
			lab.putAt("highAvailability", highAvailability)
		}		
	}
	
	/**
	 * Removes a valid IP in a lab
	 * @param lab laboratory to be modified
	 * @param ip to be removed
	 */
	def deleteIP(Laboratory lab, ip){
		def executionIp = ExecutionIP.where{id==Long.parseLong(ip)&&ipPool in lab.ipPools}.find()
		if(executionIp && (executionIp.state == IPEnum.AVAILABLE||executionIp.state == IPEnum.DISABLED)){	
			executionIp.delete()
		}
	}
	
	/**
	 * Changes the state of a IP from AVAILABLE to DISABLE and vis
	 * @param lab laboratory allows IP
	 * @param ip IP to be modified
	 */
	def setStatusIP(Laboratory lab, ip){
		def executionIp = ExecutionIP.where{id==Long.parseLong(ip)&&ipPool in lab.ipPools}.find()
		if(executionIp && (executionIp.state.equals(IPEnum.AVAILABLE)||executionIp.state.equals(IPEnum.DISABLED))){
			if(executionIp.state == IPEnum.AVAILABLE)executionIp.putAt("state", IPEnum.DISABLED)
			else if(executionIp.state != IPEnum.AVAILABLE)executionIp.putAt("state", IPEnum.AVAILABLE)
		}
	}
	
	/**
	 * Deletes a IP Pool from a lab
	 * Validates if IP Pool have ips Unavailable
	 * @param lab where is assigned ips
	 * @param pool of ip to be deleted 
	 */
	def deletePool(Laboratory lab, pool){
		def ipPool = IPPool.get(pool)
		if(ipPool&&ipPool.getUsedIpsQuantity()==0){
			for(ExecutionIP ip : ipPool.ips)deleteIP(lab, ip)
			ipPool.delete()
		}else throw new Exception('Some Ips in IP Pool are being used')
	}
	
	/**
	 * Creates a new Pool in a lab, validates if range is valid
	 * @param lab where will be assign ip's
	 * @param privateNet if network is private or public
	 * @param netGateway gateway
	 * @param netMask mask for network
	 * @param ipInit first ip 
	 * @param ipEnd last ip
	 */
	def createPool(Laboratory lab, privateNet, netGateway, netMask, ipInit, ipEnd){
		ArrayList<String> ips = createRange(ipInit,ipEnd)
		def ipPool=new IPPool(privateNet:privateNet,gateway: netGateway, mask: netMask, laboratory: lab).save()
		for(String ipFind: ips){
			new ExecutionIP(ip:ipFind,ipPool:ipPool).save()
		}
	}
	
	/**
	 * Deletes a host (physical machine) from a lab
	 * Validates if there are not deployments in host
	 * @param lab laboratory where is located the host
	 * @param host to be deleted
	 */
	def deleteHost(Laboratory lab, host){
		PhysicalMachine hostMachine = PhysicalMachine.where{id==host&&laboratory==lab}.find()
		if(hostMachine){			
			if(Execution.where{
				executionNode==hostMachine&&status!=ExecutionStateEnum.FINISHED}.findAll().size()>0) 
				throw new Exception('The Host can not be deleted because there are some deployments linked to this one') 
			def executions = Execution.where{
					executionNode==hostMachine&&status==ExecutionStateEnum.FINISHED}.findAll()
			for(Execution exe in executions)exe.putAt("executionNode", null)			
			hostMachine.delete()			
		}
	}
	
	/**
	 * Creates a task to stop, update agent or clear cache in a list of host machines
	 * Sends task for queue if it is valid
	 * @param machines
	 */
	def createRequestTasktoMachines(machines, task, user){
		if(task==null||machines.size()==0)throw new Exception("Invalid values");
		List<PhysicalMachine> machineList = new ArrayList<PhysicalMachine>();
		for(PhysicalMachine pm: machines){
			pm.putAt("state", PhysicalMachineStateEnum.PROCESSING)
			machineList.add(pm);
		}
		QueueTaskerControl.taskMachines(machineList,task, user)
	}
	
	/**
	 * Calculates the quantity of available deployments by hardware profiles
	 * @param lab where will be calculated the available resources
	 * @param hwProfiles profiles to calculate available deployments
	 * @param highAvailability if high availability resources should be calculated
	 * @param platform to filter resources
	 */
	def calculateDeploys(Laboratory lab, def hwProfiles, highAvailability, platform){
		TreeMap<String, Integer> results = new TreeMap<String,Integer>();	
		def availableIps = lab.getAvailableIps()
		lab.physicalMachines.findAll{it.state == PhysicalMachineStateEnum.ON && it.highAvailability == highAvailability?1:0 && platform in it.platforms}.each{			
			def pmId = it.id;
			//How much resources in host are available in this moment			
			def availableResources = it.availableResources()			
			for(HardwareProfile hwd in hwProfiles){
				def quantityRam = Math.floor(availableResources.ram/hwd.ram)
				def quantityCores = Math.floor(availableResources.cores/hwd.cores)
				def quantity = (quantityRam>quantityCores?quantityCores:quantityRam)
				def finalQuantity = quantity>availableResources.vms?availableResources.vms:quantity
				if(finalQuantity<0)finalQuantity = 0				
				if(results.get(hwd.name)==null)					
					results.put(hwd.name, finalQuantity)
				else
					results.put(hwd.name,results.get(hwd.name)+finalQuantity);
			}			
		}
		for(HardwareProfile hwd in hwProfiles){
			if(results.get(hwd.name)==null)results.put(hwd.name, 0);
			if(availableIps.size()<results.get(hwd.name))results.put(hwd.name, availableIps.size());
		}
		
		return results
	}
	
	/**
	 * Method used to create a valid IP Range
	 * @param ipInit first ip
	 * @param ipEnd last ip
	 * @return list of valid ip in range
	 */
	private ArrayList<String> createRange(ipInit,ipEnd){
		Ip4Validator validator = new Ip4Validator();
		if(!validator.validate(ipInit)||!validator.validate(ipEnd)||!validator.validateRange(ipInit,ipEnd))throw new Exception("IP range is not valid")
		String[] components = ipInit.split(".");
		String[] components2 = ipEnd.split(".");
		ArrayList<String> ips = new ArrayList<String>();
		String ip = ipInit;
		while(validator.inRange(ipInit, ipEnd, ip)){
			ips.add(ip);
			long ipnumber = validator.transformIp(ip)+1;
			int b1 = (ipnumber >> 24) & 0xff;
			int b2 = (ipnumber >> 16) & 0xff;
			int b3 = (ipnumber >>  8) & 0xff;
			int b4 = (ipnumber      ) & 0xff;
			ip=b1+"."+b2+"."+b3+"."+b4
		}
		return ips
	}
}
