package uniandes.unacloud.agent.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import uniandes.unacloud.agent.communication.receive.ClouderClientAttention;
import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.system.OSFactory;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Class responsible to execute commands to control agent operation
 * @author CesarF
 *
 */
public class AgentManager {
	
	private static String agentVersion;
	
	/**
	 * Responsible to execute command to run Agent Updater program
	 * @return message
	 */
	public static String updateAgent(){
		ClouderClientAttention.close();
        try {
			Runtime.getRuntime().exec(new String[]{OSFactory.getOS().getJavaCommand(),"-jar",UnaCloudConstants.UPDATER_JAR,UnaCloudConstants.DELAY+""});
        } catch (Exception e) {
        }
        new Thread() {
        	public void run() {
        		try {
        			Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		System.exit(6);
        	};
        }.start();
        return UnaCloudConstants.SUCCESSFUL_OPERATION;
	}
	
	/**
	 * Responsible to execute command to stop agent
	 * @return message 
	 */
	public static String stopAgent(){
	     ClouderClientAttention.close();
         new Thread() {
         	public void run() {
         		try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
         		System.exit(0);
         	};
         }.start();
         return UnaCloudConstants.SUCCESSFUL_OPERATION;
	}
	
	/**
	 * Responsible to respond agent version
	 * @return agent version
	 */
	public static String getVersion(){
		if(agentVersion==null){
			File versions = new File(UnaCloudConstants.VERSION_FILE);
	        try {
	            BufferedReader ver = new BufferedReader(new FileReader(versions));
	            for (String h; (h = ver.readLine()) != null &&agentVersion==null;)agentVersion = h;
	            ver.close();
	            if (agentVersion == null)agentVersion = "NOVERSION";
	        } catch (IOException ex) {
	        	agentVersion = "NOVERSION";
	        }
		}
		return agentVersion;
	}
	
	/**
	 * Sends a not scheduled message to server with extra information about physical machine state
	 */
	public static void sendStatusMessage() {
		try {     	       	   
     	   ServerMessageSender.reportPhyisicalMachine(getFreeDataSpace(), getTotalDataSpace(), null);
        } catch(Exception sce) {
     	   sce.printStackTrace();
        }
	}
	
	/**
	 * Sends initial message to server with extra information about physical machine state
	 */
	public static void sendInitialMessage() {
		try {     	       	   
     	   ServerMessageSender.reportPhyisicalMachine(getFreeDataSpace(), getTotalDataSpace(), getVersion());
        } catch(Exception sce) {
     	   sce.printStackTrace();
        }
	}


	/**
	 * Returns free space in Data path
	 * @return
	 */
	public static long getFreeDataSpace() {
		String dataPath = VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH);
		return new File(dataPath).getFreeSpace();
	}
	
	/**
	 * Returns total space in Data path
	 * @return
	 */
	public static long getTotalDataSpace() {
		String dataPath = VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.DATA_PATH);
		return new File(dataPath).getTotalSpace();
	}

}
