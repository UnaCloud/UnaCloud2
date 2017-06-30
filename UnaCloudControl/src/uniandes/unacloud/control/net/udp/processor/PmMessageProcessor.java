package uniandes.unacloud.control.net.udp.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import uniandes.unacloud.common.net.messages.udp.UDPMessageStatePM;
import uniandes.unacloud.common.net.messages.udp.UnaCloudMessageUDP;
import uniandes.unacloud.common.net.udp.UDPMessageEnum;
import uniandes.unacloud.control.ControlManager;
import uniandes.unacloud.control.db.PhysicalMachineUpdater;

/**
 * Processes message from physical machines with reports about physical machines
 * @author CesarF
 *
 */
public class PmMessageProcessor extends AbstractReceiverProcessor {

	public PmMessageProcessor(UnaCloudMessageUDP message) {
		super(message);
	}

	@Override
	public void processMessage(UnaCloudMessageUDP messageUnaCloud) throws JSONException, SQLException {
		if (messageUnaCloud.getMessage() != null && messageUnaCloud.getType().equals(UDPMessageEnum.STATE_PM)) {
			List<Long> idsToStop = null;
			try (Connection con = ControlManager.getInstance().getDBConnection();) {
				UDPMessageStatePM message = new UDPMessageStatePM(messageUnaCloud);
				if (PhysicalMachineUpdater.updatePhysicalMachine(message.getHost(), message.getHostUser(), 
						message.getIp(), message.getFreeSpace(), message.getDataSpace(), message.getVersion(), con)) {
					Long[] ids = message.getExecutions();	
					if (ids != null)
						idsToStop = PhysicalMachineUpdater.updateExecutions(ids, message.getHost(), con);				
				}			
			}catch (Exception e) {
				e.printStackTrace();
			}				
			if (idsToStop != null && idsToStop.size() > 0) {
				//Send stop machines message because executions has been reported as finished or failed to user
				Long[] idsLong = new Long[idsToStop.size()];
				for (int i = 0; i < idsLong.length; i++) 
					idsLong[i] = idsToStop.get(i);					
				ControlManager.getInstance().sendStopMessageExecutions(idsLong);
			}
		}
		else{
			System.err.println("ERROR in message: " + messageUnaCloud.getIp() + "-" + messageUnaCloud.getHost() + "-" + messageUnaCloud.getPort() + " " + (messageUnaCloud.getMessage() != null ? messageUnaCloud.getMessage().toString() : null));
		}
	}
}
