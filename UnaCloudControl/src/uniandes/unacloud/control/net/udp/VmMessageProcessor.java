package uniandes.unacloud.control.net.udp;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;

import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.tcp.message.ExecutionStateMessage;
import uniandes.unacloud.common.net.udp.AbstractUDPReceiverProcessor;
import uniandes.unacloud.common.net.udp.message.UDPMessageEnum;
import uniandes.unacloud.control.ControlManager;
import uniandes.unacloud.control.db.PhysicalMachineUpdater;

/**
 * Process message from physical machines with reports about executions
 * @author CesarF
 *
 */
public class VmMessageProcessor extends AbstractUDPReceiverProcessor{

	public VmMessageProcessor(UnaCloudMessage message) {
		super(message);
	}

	@Override
	public void processMessage(UnaCloudMessage unacloudMessage) throws JSONException, SQLException {
		System.out.println(unacloudMessage.getMessage());
		if (unacloudMessage.getMessage() != null) {
			if (unacloudMessage.getType().equals(UDPMessageEnum.STATE_EXE)) {
				try (Connection con = ControlManager.getInstance().getDBConnection();) {
					ExecutionStateMessage message = new ExecutionStateMessage(unacloudMessage);
					System.out.println("Report EXE: " + message.getHost() + " - ");
					PhysicalMachineUpdater.updateExecution(message.getExecutionCode(), message.getHost(), message.getExecutionMessage(), message.getState(), con);
				}catch (Exception e) {
					e.printStackTrace();
				}			 
			}
		}
	}

}
