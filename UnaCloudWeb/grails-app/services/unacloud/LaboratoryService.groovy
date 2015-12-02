package unacloud

import org.springframework.aop.ThrowsAdvice;

import unacloud.enums.IPEnum;
import unacloud.enums.NetworkQualityEnum;
import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.task.queue.QueueTaskerControl;

import com.losandes.utils.Ip4Validator

import grails.transaction.Transactional

@Transactional
class LaboratoryService {

	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
    /**
	 * Return the lab name list
	 * @return lab name list
	 */
	def getLabsNames(){
		return Laboratory.executeQuery("select name from Laboratory")
	}
	
	/**
	 * Return all labs searched by names array
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
	
	def addMachine(ip, name, cores, pCores, ram, osId, mac, Laboratory lab) {
		def physicalMachine = new PhysicalMachine(name:name, cores:cores, pCores:pCores, ram: ram, highAvailability:(lab.highAvailability),
			mac:mac, state: PhysicalMachineStateEnum.OFF,operatingSystem: OperatingSystem.get(osId),laboratory:lab, ip:new PhysicalIP(ip:ip))
		physicalMachine.save(failOnError:true)	
	}
	
	/**
	 * Set values in a host machine
	 * @param ip
	 * @param name
	 * @param cores
	 * @param pCores
	 * @param ram
	 * @param osId
	 * @param mac
	 * @param host
	 * @return
	 */
	def editMachine(ip, name, cores, pCores, ram, osId, mac, PhysicalMachine host){
		if(!host.ip.ip.equals(ip)){
			host.ip.setIp(ip)
		}
		host.setName(name)
		host.setMac(mac)
		host.setCores(Integer.parseInt(cores))
		host.setpCores(Integer.parseInt(pCores))
		host.setRam(Integer.parseInt(ram))
		host.setOperatingSystem(OperatingSystem.get(osId))
		host.save(failOnError:true)
	}
	
	/**
	 * Change the status of a laboratory
	 * @param lab laboratory to be edited
	 * @return
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
	 * Modified basic characteristics in lab
	 * @param lab Laboratory to be modified
	 * @param name new name of laboratory
	 * @param netConfig Network configuration
	 * @param highAvailability if lab is high availability
	 * @return
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
	 * Remove a valid IP in a lab
	 * @param lab laboratory to be modified
	 * @param ip to be removed
	 * @return
	 */
	def deleteIP(Laboratory lab, ip){
		def executionIp = ExecutionIP.where{id==Long.parseLong(ip)&&ipPool in lab.ipPools}.find()
		if(executionIp && (executionIp.state == IPEnum.AVAILABLE||executionIp.state == IPEnum.DISABLED)){	
			executionIp.delete()
		}
	}
	
	/**
	 * Change the state of a IP from AVAILABLE to DISABLE and vis
	 * @param lab laboratory allows IP
	 * @param ip IP to be modified
	 * @return
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
	 * @param lab
	 * @param pool
	 * @return
	 */
	def deletePool(Laboratory lab, pool){
		def ipPool = IPPool.get(pool)
		if(ipPool&&ipPool.getUsedIpsQuantity()==0){
			ipPool.delete()
		}else throw new Exception('Some Ips in IP Pool are being used') 
	}
	
	/**
	 * Create a new Pool in a lab, validates if range is valid
	 * @param lab
	 * @param privateNet
	 * @param netGateway
	 * @param netMask
	 * @param ipInit
	 * @param ipEnd
	 * @return
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
	 * @return
	 */
	def deleteHost(Laboratory lab, host){
		PhysicalMachine hostMachine = PhysicalMachine.where{id==host&&laboratory==lab}.find()
		if(hostMachine){
			def executions = VirtualMachineExecution.where{
				executionNode==hostMachine&&status!=VirtualMachineExecutionStateEnum.FINISHED}.findAll()
			if(VirtualMachineExecution.where{
				executionNode==hostMachine&&status!=VirtualMachineExecutionStateEnum.FINISHED}.findAll().size()>0) 
				throw new Exception('The Host can not be deleted because there are some deployments linked to this one') 
			for(VirtualMachineExecution exe in executions)exe.putAt("executionNode", null)			
			hostMachine.delete()			
		}
	}
	
	/**
	 * Create a task to stop, update agent or clear cache in a list of host machines
	 * @param machines
	 * @return
	 */
	def createRequestTasktoMachines(machines, task, user){
		List<PhysicalMachine> machineList = new ArrayList<PhysicalMachine>();
		for(PhysicalMachine pm: machines){
			pm.putAt("state", PhysicalMachineStateEnum.PROCESSING)
			machineList.add(pm);
		}
		QueueTaskerControl.taskMachines(machineList,task as String, user)
	}
	
	/**
	 * Calculate the quantity of available deployments by hardware profiles
	 * @param lab
	 * @param hwProfiles
	 * @param highAvailability
	 * @return
	 */
	def calculateDeploys(Laboratory lab, def hwProfiles, highAvailability){
		TreeMap<String, Integer> results = new TreeMap<String,Integer>();	
		lab.physicalMachines.findAll{it.state == PhysicalMachineStateEnum.ON && it.highAvailability == highAvailability?1:0}.each{			
			def pmId = it.id;
			//How much resources in host are available in this moment
			def executionValues = it.availableResources()
			for(HardwareProfile hwd in hwProfiles){
				def quantityRam = Math.floor(executionValues.ram/hwd.ram)
				def quantityCores = Math.floor(executionValues.cores/hwd.cores)
				if(results.get(hwd.name)==null)results.put(hwd.name, (quantityRam>quantityCores?quantityCores:quantityRam))
				else results.put(hwd.name,results.get(hwd.name)+(quantityRam>quantityCores?quantityCores:quantityRam));
			}			
		}
		for(HardwareProfile hwd in hwProfiles)
			if(results.get(hwd.name)==null)results.put(hwd.name, 0);
		
		return results
	}
	
	/**
	 * Method used to create a valid IP Range
	 * @param ipInit
	 * @param ipEnd
	 * @return
	 */
	private ArrayList<String> createRange(ipInit,ipEnd){
		Ip4Validator validator = new Ip4Validator();
		if(!validator.validate(ipInit)||!validator.validate(ipEnd)||!validator.validateRange(ipInit,ipEnd))throw new Exception("Ip range is not valid")
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
