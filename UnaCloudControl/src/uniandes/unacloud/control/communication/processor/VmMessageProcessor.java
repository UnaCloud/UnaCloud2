package uniandes.unacloud.control.communication.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;

import uniandes.unacloud.common.com.UDPMessageEnum;
import uniandes.unacloud.common.com.messages.udp.UDPMessageStateEXE;
import uniandes.unacloud.common.com.messages.udp.UnaCloudMessageUDP;
import uniandes.unacloud.control.db.PhysicalMachineUpdater;
import uniandes.unacloud.control.init.ControlManager;

/**
 * Process message from physical machines with reports about executions
 * @author CesarF
 *
 */
public class VmMessageProcessor extends AbstractReceiverProcessor{

	public VmMessageProcessor(UnaCloudMessageUDP message) {
		super(message);
	}

	@Override
	public void processMessage(UnaCloudMessageUDP unacloudMessage) throws JSONException, SQLException {
		System.out.println(unacloudMessage.getMessage());
		if(unacloudMessage.getMessage()!=null){
			if(unacloudMessage.getType().equals(UDPMessageEnum.STATE_EXE)){
				try(Connection con = ControlManager.getInstance().getDBConnection();){
					UDPMessageStateEXE message = new UDPMessageStateEXE(unacloudMessage);
					System.out.println("Report EXE: "+message.getHost()+" - ");
					PhysicalMachineUpdater.updateExecution(message.getExecutionCode(), message.getHost(), message.getExecutionMessage(), message.getState(), con);
				}catch (Exception e) {
					e.printStackTrace();
				}			 
			}
		}
	}

}
