package communication.send;

import com.losandes.utils.ClientConstants;
import com.losandes.utils.OperatingSystem;

import communication.UDPMessageEnum;
import communication.UnaCloudDataSenderUDP;
import communication.UnaCloudMessageUDP;
import domain.VariableManager;

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
	 * Push info by UDP protocol to server port for physical machine reports
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public boolean pushInfoPM(UDPMessageEnum type, Object...params) throws Exception{
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(ClientConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(ClientConstants.CONTROL_SERVER_PORT_PM);
		String msgParams=null;
		for(int e=0,i=params.length;e<i;e+=2)msgParams+=params[e]+"="+params[e+1]+"-";
		UnaCloudMessageUDP message = new UnaCloudMessageUDP(msgParams, serverIP, serverPort, OperatingSystem.getHostname(), type);
		return sender.sendMessage(message);		
	}
	
	/**
	 * Push info by UDP protocol to server port for virtual machines reports
	 * @param params
	 * @return
	 * @throws Exception 
	 */
	public boolean pushInfoVM(UDPMessageEnum type, Object...params) throws Exception{
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(ClientConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(ClientConstants.CONTROL_SERVER_PORT_VM);
		String msgParams=null;
		for(int e=0,i=params.length;e<i;e+=2)msgParams+=params[e]+"="+params[e+1]+"-";
		UnaCloudMessageUDP message = new UnaCloudMessageUDP(msgParams, serverIP, serverPort, OperatingSystem.getHostname(), type);
		return sender.sendMessage(message);		
	}
}
