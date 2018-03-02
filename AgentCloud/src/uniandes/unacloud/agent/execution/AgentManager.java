package uniandes.unacloud.agent.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.agent.net.receive.ClouderClientAttention;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.net.tcp.message.UnaCloudResponse;
import uniandes.unacloud.common.utils.UnaCloudConstants;
import uniandes.unacloud.utils.LocalProcessExecutor;

/**
 * Class responsible to execute commands to control agent operation
 * @author CesarF
 *
 */
public class AgentManager {
	
	/**
	 * Current agent version
	 */
	private static String agentVersion;
	
	
	/**
	 * Responsible to execute command to run Agent Updater program
	 * @return message
	 */
	public static UnaCloudResponse updateAgent() {
		System.out.println("Updating agent");
		try {
			ClouderClientAttention.getInstance().stopService();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			LocalProcessExecutor.executeCommand(new String[]{OSFactory.getOS().getJavaCommand(), "-jar", UnaCloudConstants.UPDATER_JAR, UnaCloudConstants.DELAY + ""});
		} catch (Exception e) {
        	e.printStackTrace();
        }
        new Thread() {
        	public void run() {
        		SystemUtils.sleep(2000);        		
        		System.exit(6);
        	};
        }.start();
        return new UnaCloudResponse(UnaCloudConstants.SUCCESSFUL_OPERATION, ExecutionProcessEnum.SUCCESS);          
	}
	
	/**
	 * Responsible to execute command to stop agent
	 * @return message 
	 */
	public static UnaCloudResponse stopAgent() {
		System.out.println("Stopping agent");
		try {
			ClouderClientAttention.getInstance().stopService();
		} catch (Exception e) {
			e.printStackTrace();
		}
         new Thread() {
         	public void run() {
         		SystemUtils.sleep(2000);         		
         		System.exit(0);
         	};
         }.start();
         return new UnaCloudResponse(UnaCloudConstants.SUCCESSFUL_OPERATION, ExecutionProcessEnum.SUCCESS); 
	}
	
	/**
	 * Responsible to respond agent version
	 * @return agent version
	 */
	public static String getVersion() {
		if (agentVersion == null) {
			File versions = new File(UnaCloudConstants.VERSION_FILE);
	        try {
	            BufferedReader ver = new BufferedReader(new FileReader(versions));
	            for (String h; (h = ver.readLine()) != null && agentVersion == null;) 
	            	agentVersion = h;
	            ver.close();
	            if (agentVersion == null)
	            	agentVersion = "NOVERSION";
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
