package uniandes.unacloud.web.utils.groovy

import java.util.ArrayList;

import uniandes.unacloud.common.utils.Ip4Validator;

/**
 * Class to manager all utilities methods for web project in groovy
 * @author CesarF
 *
 */
class Utils {
	/**
	 * Method to validate if an string is not null and it is not empty
	 * @param text
	 * @return
	 */
	def validate(text) {
		if (text || text.isEmpty()) 
			return false
		else 
			return true
	}
	
	/**
	 * Method used to create a valid IP Range
	 * @param ipInit first ip
	 * @param ipEnd last ip
	 * @return list of valid ip in range
	 */
	private ArrayList<String> createRange(String ipInit, String ipEnd){
		Ip4Validator validator = new Ip4Validator();
		if (!validator.validate(ipInit) || !validator.validate(ipEnd) || !validator.validateRange(ipInit, ipEnd))
				throw new Exception("IP range is not valid")
		String[] components = ipInit.split(".");
		String[] components2 = ipEnd.split(".");
		ArrayList<String> ips = new ArrayList<String>();
		String ip = ipInit;
		while (validator.inRange(ipInit, ipEnd, ip)) {
			ips.add(ip);
			long ipnumber = validator.transformIp(ip) + 1;
			int b1 = (ipnumber >> 24) & 0xff;
			int b2 = (ipnumber >> 16) & 0xff;
			int b3 = (ipnumber >>  8) & 0xff;
			int b4 = (ipnumber      ) & 0xff;
			ip = b1 + "." + b2 + "." + b3 + "." + b4
		}
		return ips
	}
}
