package uniandes.unacloud.control.net.udp;

import java.sql.Connection;
import java.util.List;

import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.udp.AbstractUDPReceiverProcessor;
import uniandes.unacloud.common.net.udp.message.UDPMessageEnum;
import uniandes.unacloud.common.net.udp.message.MachineStateMessage;
import uniandes.unacloud.control.ControlManager;
import uniandes.unacloud.share.db.ExecutionManager;
import uniandes.unacloud.share.db.PhysicalMachineManager;

/**
 * Processes message from physical machines with reports about physical machines
 * @author CesarF
 *
 */
public class PmMessageProcessor extends AbstractUDPReceiverProcessor {

	public PmMessageProcessor(UnaCloudMessage message) {
		super(message);
	}

	@Override
	public void processMessage(UnaCloudMessage uMessage) throws Exception {
		if (uMessage.getType() != null && uMessage.getType().equals(UDPMessageEnum.STATE_PM.name())) {
			List<Long> idsToStop = null;
			MachineStateMessage message = (MachineStateMessage)uMessage;
			try (Connection con = ControlManager.getInstance().getDBConnection();) {				
				if (PhysicalMachineManager.updatePhysicalMachine(message.getHost(), message.getHostUser(), 
						message.getIp(), message.getFreeSpace(), message.getDataSpace(), message.getVersion(), con)) {
					Long[] ids = message.getExecutions();	
					if (ids != null)
						idsToStop = ExecutionManager.updateExecutions(message.getHost(), ids, con);				
				}			
			} catch (Exception e) {
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
		else {
			System.err.println("ERROR in message: " + uMessage.getIp() + " - " + uMessage.getHost() + " - " + uMessage.toString() );
		}
	}
}
