package com.losandes.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * IPAddress validator Testing
 * @author mkyong
 *
 */
public class Ip4Validator {
	  private Pattern pattern;
	  private Matcher matcher;
	 
	  private static final String IPADDRESS_PATTERN = 
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
			"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	 
	  public Ip4Validator(){
		  pattern = Pattern.compile(IPADDRESS_PATTERN);
	  }
	 
	   /**
	    * Validate ip address with regular expression
	    * @param ip ip address for validation
	    * @return true valid ip address, false invalid ip address
	    */
	  public boolean validate(String ip){		  
		  matcher = pattern.matcher(ip);
		  return matcher.matches();	    	    
	  }
	  
	  public boolean validateRange(String ip1, String ip2) throws UnknownHostException{
		  long a = transformIp((Inet4Address) InetAddress.getByName(ip1));
		  long b = transformIp((Inet4Address) InetAddress.getByName(ip2));			 
		  return b>=a;
	  }
	  
	  public boolean inRange(String ip1, String ip2, String ipTest) throws UnknownHostException{
		  long a = transformIp((Inet4Address) InetAddress.getByName(ip1));
		  long b = transformIp((Inet4Address) InetAddress.getByName(ip2));	
		  long c = transformIp((Inet4Address) InetAddress.getByName(ipTest));
		  return c>=a&&c<=b;
	  }
	  public long transformIp(String ip) throws UnknownHostException{
		  return transformIp((Inet4Address) InetAddress.getByName(ip));
	  }
	  
	  public long transformIp(Inet4Address ip){
		  byte[] octets = ip.getAddress();
	      long result = 0;
	      for (byte octet : octets) {
	         result <<= 8;
	         result |= octet & 0xff;
	      }
	      return result;
	  }
}
