package uniandes.communication.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;

import uniandes.ControlManager;
import uniandes.db.PhysicalMachineUpdater;
import communication.UDPMessageEnum;
import communication.messages.udp.UDPMessageStateVM;
import communication.messages.udp.UnaCloudMessageUDP;

/**
 * Process message from physical machines with reports about virtual machines
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
			if(unacloudMessage.getType().equals(UDPMessageEnum.STATE_VM)){
				try(Connection con = ControlManager.getInstance().getDBConnection();){
					UDPMessageStateVM message = new UDPMessageStateVM(unacloudMessage);
					System.out.println("Report VM: "+message.getHost()+" - ");
					PhysicalMachineUpdater.updateVirtualExecution(message.getVirtualMachineCode(), message.getHost(), message.getMessageExecution(), message.getState(), con);
				}catch (Exception e) {
					e.printStackTrace();
				}			 
			}
		}
	}

}
