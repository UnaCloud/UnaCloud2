package uniandes.unacloud.web.services

import uniandes.unacloud.web.domain.IP

import java.util.ArrayList;

import uniandes.unacloud.common.utils.Ip4Validator;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.web.domain.ExecutionIP;
import uniandes.unacloud.web.domain.IPPool;
import uniandes.unacloud.web.domain.Laboratory;
import uniandes.unacloud.web.domain.NetInterface;
import uniandes.unacloud.web.utils.groovy.Utils;
import grails.transaction.Transactional

@Transactional
class IpService {

   /**
	 * Removes a valid IP in a lab
	 * @param lab laboratory to be modified
	 * @param ip to be removed
	 */
	def delete(Laboratory lab, ip) {
        print "A"
		ExecutionIP executionIp = ExecutionIP.where{id == ip && ipPool in lab.ipPools}.find()
		if (executionIp.ipPool.ips.size() == 1)
			throw new Exception("IP range must have one IP address at least")
		if (executionIp && (executionIp.state == IPEnum.AVAILABLE || executionIp.state == IPEnum.DISABLED)) {	
			NetInterface.executeUpdate("update NetInterface net set net.ip = null where net.ip.id = :id", [id : executionIp.id]);
			IPPool pool = executionIp.ipPool
            print "C"
			pool.removeFromIps(executionIp)
            print "D"
			executionIp.delete()
            print "E"
		}
        print "B"
	}
	
	/**
	 * Changes the state of a IP from AVAILABLE to DISABLE and vis
	 * @param lab laboratory allows IP
	 * @param ip IP to be modified
	 */
	def updateStatus(Laboratory lab, ip){
		def executionIp = ExecutionIP.where{id == ip && ipPool in lab.ipPools}.find()
		if (executionIp && (executionIp.state.equals(IPEnum.AVAILABLE) || executionIp.state.equals(IPEnum.DISABLED))) {
			if (executionIp.state == IPEnum.AVAILABLE)
				executionIp.putAt("state", IPEnum.DISABLED)
			else if (executionIp.state != IPEnum.AVAILABLE)
				executionIp.putAt("state", IPEnum.AVAILABLE)
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
		print ipPool.id+" "+ipPool.getUsedIpsQuantity()
		if (ipPool && ipPool.getUsedIpsQuantity() == 0) {
            def ips=[]
            for (ExecutionIP ip : ipPool.ips)
			{
                print "EXEC "+ ip.id
                delete(lab, ip.id)
                print "SALE "+ip.id
			}
			print "Del"
			ipPool.delete()
			print "del"
		} else
			throw new Exception('Some IP addresses in IP Pool are being used')
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
	def createPool(Laboratory lab, privateNet, netGateway, netMask, ipInit, ipEnd) throws Exception {
		ArrayList<String> ips = Utils.createRange(ipInit,ipEnd)
		if (ips.size() == 0)
			throw new Exception("IP range invalid")
		def ipPool = new IPPool(privateNet:privateNet, gateway: netGateway, mask: netMask, laboratory: lab).save()
		for (String ipFind : ips)
			new ExecutionIP(ip : ipFind, ipPool : ipPool).save()
	}
	
}
