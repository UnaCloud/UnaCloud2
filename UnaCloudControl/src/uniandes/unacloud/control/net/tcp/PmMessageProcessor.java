package uniandes.unacloud.control.net.tcp;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;

import uniandes.unacloud.common.net.UnaCloudMessage;
import uniandes.unacloud.common.net.tcp.AbstractTCPSocketProcessor;
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
public class PmMessageProcessor extends AbstractTCPSocketProcessor {

	public PmMessageProcessor(Socket socket) {
		super(socket);
	}

	@Override
	public void processMessage(Socket socket) throws Exception {
		
		ObjectInputStream ios = new ObjectInputStream(socket.getInputStream());
		UnaCloudMessage messageUnaCloud = (UnaCloudMessage) ios.readObject();
		if (messageUnaCloud.getMessage() != null && messageUnaCloud.getType().equals(UDPMessageEnum.STATE_PM)) {
			List<Long> idsToStop = null;
			try (Connection con = ControlManager.getInstance().getDBConnection();) {
				MachineStateMessage message = new MachineStateMessage(messageUnaCloud);
				if (PhysicalMachineManager.updatePhysicalMachine(message.getHost(), message.getHostUser(), 
						message.getIp(), message.getFreeSpace(), message.getDataSpace(), message.getVersion(), con)) {
					Long[] ids = message.getExecutions();	
					if (ids != null)
						idsToStop = ExecutionManager.updateExecutions(ids, message.getHost(), con);				
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
		else{
			System.err.println("ERROR in message: " + messageUnaCloud.getIp() + "-" + messageUnaCloud.getHost() + "-" + messageUnaCloud.getPort() + " " + (messageUnaCloud.getMessage() != null ? messageUnaCloud.getMessage().toString() : null));
		}
	}
}
