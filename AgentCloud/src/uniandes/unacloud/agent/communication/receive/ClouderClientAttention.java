package uniandes.unacloud.agent.communication.receive;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import uniandes.unacloud.agent.execution.task.ExecutorService;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Responsible for listening messages from Server
 * @author CesarF
 * @author Clouder
 */
public class ClouderClientAttention{
    
	//-----------------------------------------------------------------
	// Variables
	//-----------------------------------------------------------------

    private ServerSocket serverSocket;
    /**
     * Port where socket is listening
     */
    private static int localPort;
    
    private static ClouderClientAttention instance;    
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

   
    
    public synchronized static ClouderClientAttention getInstance() throws Exception {
        if(instance==null)instance=new ClouderClientAttention();
            return instance;
    }
    
    /**
     * Responsible for obtaining data connection and listening to Server
     * @throws Exception 
     */
    private ClouderClientAttention() throws Exception{
        localPort = VariableManager.getInstance().getGlobal().getIntegerVariable(UnaCloudConstants.AGENT_PORT);      
    }

    /**
     * Responsible for connecting with Server and start a communication thread
     */
    public final void start() {
        try {
			serverSocket = new ServerSocket(localPort);
			System.out.println("Listening in "+localPort);
	        while (true) {
	        	try{
	        		Socket s=serverSocket.accept();
	        		ExecutorService.executeRequestTask(new ClouderServerAttentionThread(s));
	            }catch(SocketException ex){
	            	break;
	            }catch (IOException ex) {
	            	ex.printStackTrace();
	            }
	        }
        } catch (IOException ex) {
        	ex.printStackTrace();
        }
    }

    /**
     * Stops the request listening process
     */
    public static void close() {
        try {
            instance.serverSocket.close();
        } catch (Exception e) {
        }
    }
}