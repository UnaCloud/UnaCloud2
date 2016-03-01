package uniandes.unacloud.communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.sql.Connection;

import com.losandes.utils.UnaCloudConstants;

import uniandes.unacloud.FileManager;
import uniandes.unacloud.db.ServerVariableManager;
import uniandes.unacloud.db.entities.ServerVariableEntity;
import uniandes.unacloud.files.AgentManager;

/**
 * Class used to process task to update agent
 * @author Cesar
 *
 */
public class AgentProcessorTask implements Runnable{
	
	private Socket socket;
	
	public AgentProcessorTask(Socket s) {
		this.socket = s;
	}

	@Override
	public void run() {
		try(Socket ss=socket; DataOutputStream out=new DataOutputStream(socket.getOutputStream()); DataInputStream is = new DataInputStream(socket.getInputStream());Connection con = FileManager.getInstance().getDBConnection();) {
			if(is.readInt()==UnaCloudConstants.REQUEST_AGENT_VERSION){
				ServerVariableEntity variable = ServerVariableManager.getVariable(con, UnaCloudConstants.AGENT_VERSION);
				out.writeUTF(variable.getValue());
				int respond = is.readInt();
				if(respond==UnaCloudConstants.GIVE_ME_FILES){
					AgentManager.copyAgentOnStream(out, con);
				}				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
