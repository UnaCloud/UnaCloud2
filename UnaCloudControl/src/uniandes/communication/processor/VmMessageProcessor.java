package uniandes.communication.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.losandes.enums.VirtualMachineExecutionStateEnum;

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
	public void processMessage(UnaCloudMessageUDP UnacloudMessage) throws JSONException, SQLException {
		System.out.println(UnacloudMessage.getMessage());
		if(UnacloudMessage.getMessage()!=null){
			if(UnacloudMessage.getType().equals(UDPMessageEnum.STATE_VM)){
				try(Connection con = ControlManager.getInstance().getDBConnection();){
					UDPMessageStateVM message = new UDPMessageStateVM(UnacloudMessage);
					System.out.println("Report VM: "+message.getHost()+" - ");
					PhysicalMachineUpdater.updateVirtualExecution(message.getVirtualMachineCode(), message.getHost(), message.getMessageExecution(), message.getState(), con);
				}catch (Exception e) {
					e.printStackTrace();
				}			
			}
		}
	}

}
