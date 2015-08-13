package utils;

import physicalmachine.Network;
import communication.AbstractGrailsCommunicator;

public class UnaCloudLogger {
	
	/**
	 * sends a log message to server
	 * @param component component which provided the message 
	 * @param message log message
	 */
	public static void log(String component,String message){
		AbstractGrailsCommunicator.pushInfo("UnaCloudServices/clouderClientAttention","hostname",Network.getHostname(),"component",component,message,message);
	}
}
