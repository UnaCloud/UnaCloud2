package uniandes.communication.processor;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.losandes.enums.VirtualMachineExecutionStateEnum;

import uniandes.ControlManager;
import uniandes.db.PhysicalMachineUpdater;
import communication.UDPMessageEnum;
import communication.UnaCloudMessageUDP;

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
	public void processMessage(UnaCloudMessageUDP message) throws JSONException, SQLException {
		System.out.println(message.getMessage());
		if(message.getMessage()!=null){
			JSONObject jsonMessage = new JSONObject(message.getMessage());
			message.setType(UDPMessageEnum.getType(jsonMessage.getString("type")));
			jsonMessage = jsonMessage.getJSONObject("data");
			if(message.getType().equals(UDPMessageEnum.STATE_VM)){
				try(Connection con = ControlManager.getInstance().getDBConnection();){
					System.out.println(jsonMessage.toString());
					//UDPMessageEnum.STATE_VM, "hostname",OperatingSystem.getHostname(),"executionId",virtualMachineCode,"state",state.toString(),"message",message
					System.out.println("Report VM: "+message.getHost()+" - "+jsonMessage.get("hostname"));
					PhysicalMachineUpdater.updateVirtualExecution(jsonMessage.getLong("executionId"),jsonMessage.getString("hostname"), jsonMessage.getString("message"), VirtualMachineExecutionStateEnum.getEnum(jsonMessage.getString("state")), con);
				}catch (Exception e) {
					e.printStackTrace();
				}			
			}
		}
	}

}
