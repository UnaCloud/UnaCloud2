package unacloud2

import unacloud2.enums.PhysicalMachineStateEnum;
import enums.MonitoringStatus;

import com.losandes.utils.Ip4Validator;

class LaboratoryService {
	
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------
	
	/**
	 * Adds a new machine to a given laboratory
	 * @param ip physical machine's IP
	 * @param name physical machine's name
	 * @param cores physical machine's number of processors
	 * @param ram physical machine's RAM memory
	 * @param disk physical machine's available disk space
	 * @param osId physical machine's operating system
	 * @param mac physical machine's MAC address
	 * @param labId physical machine's laboratory id
	 */
    
	def addMachine(ip, name, cores, ram, disk, osId, mac, labId) {
		def machineIP=new IP(ip:ip, used:true)
		machineIP.save(failOnError:true)
		def lab= Laboratory.get(labId)
		println "creating machine in laboratory"+ lab.name+"-"+lab.highAvailability
		def physicalMachine = new PhysicalMachine( lastReport: new Date(),name:name, state: PhysicalMachineStateEnum.OFF, cores:cores,
			ram: ram, hardDisk: disk, highAvailability:(lab.highAvailability), 
			ip:machineIP, operatingSystem: OperatingSystem.get(osId), mac:mac,monitorStatus:MonitoringStatus.DISABLE,monitorStatusEnergy:MonitoringStatus.DISABLE)
		
		physicalMachine.laboratory=lab
		physicalMachine.save(failOnError:true)
		lab.physicalMachines.add(physicalMachine)
		lab.save()
    }
	
	/**
	 * Changes a physical machine's values
	 * @param machine machine to be edited
	 * @param name new machine name
	 * @param ip new machine IP address
	 * @param osId new machine operating system value
	 * @param cores mew machine processors value
	 * @param mac new machine MAC address 
	 * @param ram new machine RAM memory value
	 * @param disk new machine disk available size
	 */
	
	def setValues(PhysicalMachine machine, name, ip,osId,cores,mac,ram,disk){
		machine.putAt("name", name)
		if(!machine.ip.ip.equals(ip)){
			machine.ip.delete()
			def newIp= new IP(ip:ip, used:true)
			newIp.save()
			machine.putAt("ip", newIp)
		}
		machine.putAt("operatingSystem", OperatingSystem.get(osId))
		machine.putAt("cores", Integer.parseInt(cores))
		machine.putAt("mac", mac)
		machine.putAt("ram", Integer.parseInt(ram))
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
	
	def createLab(name, highAvailability,netConfig, virtual, netGateway, netMask, ipInit, ipEnd){
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
		def ipPool=new IPPool(virtual:virtual,gateway: netGateway, mask: netMask).save()
		new Laboratory (virtualMachinesIPs: ipPool, name: name, highAvailability: highAvailability,networkQuality: netConfig ).save();
		for(String ipFind: ips){
			new IP(ip:ipFind,ipPool:ipPool,used:false).save()
		}
	}
}
