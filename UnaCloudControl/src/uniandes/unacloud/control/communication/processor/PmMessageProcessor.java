package uniandes.unacloud.control.communication.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;

import uniandes.unacloud.common.com.UDPMessageEnum;
import uniandes.unacloud.common.com.messages.udp.UDPMessageStatePM;
import uniandes.unacloud.common.com.messages.udp.UnaCloudMessageUDP;
import uniandes.unacloud.control.db.PhysicalMachineUpdater;
import uniandes.unacloud.control.init.ControlManager;

/**
 * Processes message from physical machines with reports about physical machines
 * @author CesarF
 *
 */
public class PmMessageProcessor extends AbstractReceiverProcessor{

	public PmMessageProcessor(UnaCloudMessageUDP message) {
		super(message);
	}

	@Override
	public void processMessage(UnaCloudMessageUDP messageUnaCloud) throws JSONException, SQLException {
		if(messageUnaCloud.getMessage()!=null){
			if(messageUnaCloud.getType().equals(UDPMessageEnum.STATE_PM)){
				try(Connection con = ControlManager.getInstance().getDBConnection();){
					UDPMessageStatePM message = new UDPMessageStatePM(messageUnaCloud);
					Long[] ids = message.getExecutions();
					
					if(PhysicalMachineUpdater.updatePhysicalMachine(message.getHost(), message.getHostUser(), message.getIp(), con)){
						List<Long> idsToStop = PhysicalMachineUpdater.updateExecutions(ids, message.getHost(), con);
						if(idsToStop!=null&&idsToStop.size()>0){
							//Send stop machines message because executions has been reported as finished or failed to user
							Long[] idsLong = new Long[idsToStop.size()];
							for (int i = 0; i < idsLong.length; i++) {
								idsLong[i]=idsToStop.get(i);
							}
							ControlManager.getInstance().sendStopMessageExecutions(idsLong);
						}
					}			
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
	}
}
