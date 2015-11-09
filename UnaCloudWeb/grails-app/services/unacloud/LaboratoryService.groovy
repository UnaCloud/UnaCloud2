package unacloud

import unacloud.enums.NetworkQualityEnum;

import com.losandes.utils.Ip4Validator

import grails.transaction.Transactional

@Transactional
class LaboratoryService {

    /**
	 * Return the list name of labs
	 */
	def getLabsNames(){
		return Laboratory.executeQuery("select name from Laboratory")
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
		Laboratory lab = new Laboratory (name: name, highAvailability: highAvailability,networkQuality: netConfig, ipPools:[],physicalMachines:[]).save();
		def ipPool=new IPPool(privateNet:privateNet,gateway: netGateway, mask: netMask, laboratory: lab).save()		
		for(String ipFind: ips){
			new ExecutionIP(ip:ipFind,ipPool:ipPool).save()
		}
	}
}
