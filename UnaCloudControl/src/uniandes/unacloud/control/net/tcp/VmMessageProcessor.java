package uniandes.unacloud.control.net.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;

import uniandes.unacloud.common.net.tcp.AbstractTCPSocketProcessor;
import uniandes.unacloud.common.net.tcp.message.ExecutionStateMessage;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.net.udp.message.UDPMessageEnum;
import uniandes.unacloud.control.ControlManager;
import uniandes.unacloud.share.db.ExecutionManager;
import uniandes.unacloud.share.db.entities.ExecutionEntity;

/**
 * Process message from physical machines with reports about executions
 * @author CesarF
 *
 */
public class VmMessageProcessor extends AbstractTCPSocketProcessor {

	public VmMessageProcessor(Socket socket) {
		super(socket);
	}

	@Override
	public void processMessage(Socket socket) throws Exception {
			
		ObjectInputStream ios = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		ExecutionStateMessage uMessage = (ExecutionStateMessage)ios.readObject();
		System.out.println("Receive VM message: " + uMessage.toString());
		if (uMessage.getType() != null && uMessage.getType().equals(UDPMessageEnum.STATE_EXE.name())) {
			try (Connection con = ControlManager.getInstance().getDBConnection();) {
				ExecutionStateMessage message = (ExecutionStateMessage) uMessage;
				System.out.println("Report EXE: " + message.getHost() + " - ");
				ExecutionEntity exe = new ExecutionEntity(message.getExecutionCode(), 0, 0, null, null, message.getState(), message.getHost(), message.getExecutionMessage());
				ExecutionManager.updateExecution(exe, null, con);
				oos.writeObject(new UnaCloudResponse("Message processed"));
			} catch (Exception e) {
				e.printStackTrace();
				oos.writeObject(new UnaCloudResponse("Error: " + e.getMessage()));
			}			 
		}
		else 
			oos.writeObject(new UnaCloudResponse("Error: message does not have a correct format"));
		
	}	

}
