package communication.send;

import com.losandes.utils.ClientConstants;
import com.losandes.utils.OperatingSystem;
import com.losandes.utils.VariableManager;

import communication.UDPMessageEnum;
import communication.UnaCloudDataSenderUDP;
import communication.UnaCloudMessageUDP;

/**
 * Singleton class to send message to server 
 * @author Cesar
 *
 */
public class UDPCommunicator {

	private UnaCloudDataSenderUDP sender;
	
	private static UDPCommunicator instance;
	
	public static UDPCommunicator getInstance(){
		if(instance == null)instance = new UDPCommunicator();
		return instance;
	}
	
	private UDPCommunicator() {
		sender = new UnaCloudDataSenderUDP();
	}
	
	/**
	 * Push info by UDP protocol to server
	 * @param params
	 * @return
	 */
	public boolean pushInfo(UDPMessageEnum type, Object...params){
		String serverIP=VariableManager.global.getStringValue(ClientConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.global.getIntValue(ClientConstants.CONTROL_SERVER_PORT);
		String msgParams=null;
		for(int e=0,i=params.length;e<i;e+=2)msgParams+=params[e]+"="+params[e+1]+"-";
		UnaCloudMessageUDP message = new UnaCloudMessageUDP(msgParams, serverIP, serverPort, OperatingSystem.getHostname(), type);
		return sender.sendMessage(message);		
	}
}
