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
		return pushInfo(serverIP, serverPort, type, params);	
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
		return pushInfo(serverIP, serverPort, type, params);
	}
	
	/**
	 * 
	 * @param ip
	 * @param port
	 * @param type
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private boolean pushInfo(String ip, int port,UDPMessageEnum type, Object...params)throws Exception{
		String msgParams="{";
		for(int e=0,i=params.length;e<i;e+=2)msgParams+=","+"\""+params[e]+"\":\""+params[e+1]+"\"";
		msgParams.replaceFirst(",", "");
		msgParams+="}";
		UnaCloudMessageUDP message = new UnaCloudMessageUDP(msgParams, ip, port, OperatingSystem.getHostname(), type);
		return sender.sendMessage(message);		
	}
}
