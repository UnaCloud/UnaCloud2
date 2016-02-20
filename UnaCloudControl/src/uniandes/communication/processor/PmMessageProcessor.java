package uniandes.communication.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONArray;
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
		if(message.getType().equals(UDPMessageEnum.STATE_PM)){
			Connection con = ControlManager.getInstance().getDBConnection();
			JSONObject jsonMessage = new JSONObject(message.getMessage());
			System.out.println(jsonMessage.toString());
			System.out.println("Report PM: "+message.getHost()+" - "+jsonMessage.get("hostname"));
			Long[] ids = new Long[jsonMessage.getJSONArray("executions").length()];
			JSONArray array =  jsonMessage.getJSONArray("executions");
			for (int i = 0; i < ids.length; i++) {
				ids[i]=array.getLong(i);
			}
			if(PhysicalMachineUpdater.updatePhysicalMachine(jsonMessage.getString("hostname"), jsonMessage.getString("hostuser"), con))
				PhysicalMachineUpdater.updateVirtualMachinesExecutions(ids, jsonMessage.getString("hostname"), con);
			
			con.close();			
		}
	}

}
