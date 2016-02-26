package uniandes.communication.processor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import uniandes.ControlManager;
import uniandes.db.PhysicalMachineUpdater;
import communication.UDPMessageEnum;
import communication.UnaCloudMessageUDP;

/**
 * Process message from physical machines with reports about physical machines
 * @author Cesar
 *
 */
public class PmMessageProcessor extends AbstractReceiverProcessor{

	public PmMessageProcessor(UnaCloudMessageUDP message) {
		super(message);
	}

	@Override
	public void processMessage(UnaCloudMessageUDP message) throws JSONException, SQLException {
		if(message.getMessage()!=null){
			JSONObject jsonMessage = new JSONObject(message.getMessage());
			message.setType(UDPMessageEnum.getType(jsonMessage.getString("type")));
			jsonMessage = jsonMessage.getJSONObject("data");
			if(message.getType().equals(UDPMessageEnum.STATE_PM)){
				Connection con = ControlManager.getInstance().getDBConnection();
				System.out.println("Report PM: "+message.getHost()+" - "+message.getMessage());
				//JSONObject executions = jsonMessage.getJSONObject("executions");
				Long[] ids = new Long[0];
				String executions = jsonMessage.getString("executions").replace("[", "").replace("]","").trim();
				if(!executions.isEmpty()){
					String[] idsS = executions.split(",");
					ids = new Long[idsS.length];
					for (int i = 0; i < ids.length; i++) {
						ids[i]=Long.parseLong(idsS[i]);
					}
				}
				if(PhysicalMachineUpdater.updatePhysicalMachine(jsonMessage.getString("hostname"), jsonMessage.getString("hostuser"), con)){
					List<Long> idsToStop = PhysicalMachineUpdater.updateVirtualMachinesExecutions(ids, jsonMessage.getString("hostname"), con);
					if(idsToStop!=null&&idsToStop.size()>0){
						//Send stop machines message because executions has been reported as finished or failed to user
						ControlManager.getInstance().sendStopMessageExecutions((Long[]) idsToStop.toArray());
					}
				}									
				con.close();			
			}
		}
		
	}

}
