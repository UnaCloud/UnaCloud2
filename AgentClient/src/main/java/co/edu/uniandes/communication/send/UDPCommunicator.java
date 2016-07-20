package co.edu.uniandes.communication.send;

import co.edu.uniandes.utils.VariableManager;

import com.losandes.utils.OperatingSystem;
import com.losandes.utils.UnaCloudConstants;

import communication.UDPMessageEnum;
import communication.UnaCloudDataSenderUDP;
import communication.UnaCloudMessageUDP;

/**
 * Singleton class to send message to server using UDP protocol
 * @author CesarF
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
	 * @return true if message was sent, false in case not
	 * @throws Exception 
	 */
	public boolean pushInfoPM(UDPMessageEnum type, Object...params) throws Exception{
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_PM_PORT);
		return pushInfo(serverIP, serverPort, type, params);	
	}
	
	/**
	 * Push info by UDP protocol to server port for virtual machines reports
	 * @param params
	 * @return Starts and configures a virtual machine. this method must be used by other methods to configure, start and schedule a virtual machine execution
	 * @throws Exception 
	 */
	public boolean pushInfoVM(UDPMessageEnum type, Object...params) throws Exception{		
		String serverIP=VariableManager.getInstance().getGlobal().getStringVariable(UnaCloudConstants.CONTROL_SERVER_IP);
		int serverPort =VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.CONTROL_MANAGE_VM_PORT);
		return pushInfo(serverIP, serverPort, type, params);
	}
	
	/**
	 * Sends message to server
	 * @param ip
	 * @param port
	 * @param type
	 * @param params
	 * @return true in case message was sent, false in case not
	 * @throws Exception
	 */
	private boolean pushInfo(String ip, int port,UDPMessageEnum type, Object...params)throws Exception{
		String msgParams="{";
		for(int e=0,i=params.length;e<i;e+=2)msgParams+=","+"\""+params[e]+"\":\""+params[e+1]+"\"";
		msgParams=msgParams.replaceFirst(",", "");
		msgParams+="}";	
		UnaCloudMessageUDP message = new UnaCloudMessageUDP(msgParams, ip, port, OperatingSystem.getHostname(), type);
		return sender.sendMessage(message);		
	}
}
