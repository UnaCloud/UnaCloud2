package communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import com.losandes.utils.VariableManager;

import tasks.ExecutorService;

/**
 * Responsible for listening to the Clouder Server
 */
public class ClouderClientAttention{
    
	//-----------------------------------------------------------------
	// Variables
	//-----------------------------------------------------------------

    private ServerSocket serverSocket;
    /**
     * Port to be used by the listening server socket
     */
    private static int localPort;
    
	//-----------------------------------------------------------------
	// Methods
	//-----------------------------------------------------------------

    private static ClouderClientAttention instance;
    public synchronized static ClouderClientAttention getInstance() {
            if(instance==null)instance=new ClouderClientAttention();
                return instance;
        }
    /**
     * Responsible for obtaining data connection and listening to Clouder Server
     */
    private ClouderClientAttention() {
        localPort = VariableManager.global.getIntValue("CLOUDER_CLIENT_PORT");
    }

    /**
     * Responsible for connecting with Clouder Server and start a communication thread
     */
    public final void connect() {
        try {
			serverSocket = new ServerSocket(localPort);
			System.out.println("Escuchando en "+localPort);
	        while (true) {
	        	try{
	        		Socket s=serverSocket.accept();
	        		ExecutorService.executeRequestTask(new ClouderServerAttentionThread(s));
	            }catch(SocketException ex){
	            	break;
	            }
	        	catch (IOException ex) {
	            	ex.printStackTrace();
	            }
	        }
        } catch (IOException ex) {
        	ex.printStackTrace();
        	//System.exit(0);
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