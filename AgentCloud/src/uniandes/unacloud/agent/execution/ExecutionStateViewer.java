package uniandes.unacloud.agent.execution;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.agent.utils.SystemUtils;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;

/**
 * This class is responsible for checking if an execution has been correctly deployed. That is, if the execution has started and if it has well configured its IP address
 * @author Clouder
 */
public class ExecutionStateViewer {
	
	/**
	 * Execution ID
	 */
	private long executionCode;
	
	/**
	 * IP to be check
	 */
	private String vmIP;
	
	/**
	 * constructs an ExecutionStateViewer for the given execution.
	 * @param executionCode The execution id to make the execution status report to the server
     * @param vmIP The IP address to check if the execution is accessible
	 */
	public ExecutionStateViewer(long executionCode,String vmIP) {
		this.executionCode = executionCode;
		this.vmIP = vmIP;
	}

    /**
     *  On this method the execution is checked and the state is reported to UnaCloud server
     */
    public boolean check() {
    	System.out.println("Start checking by ip to " + vmIP);
        boolean red = false;
        SystemUtils.sleep(3000);
       
        for (int e = 0; e < 8 && !red; e++)
            if (!(red = pingVerification(vmIP)))
            	 SystemUtils.sleep(30000);        
        try {
        	if (red) {
        		ServerMessageSender.reportExecutionState(executionCode, ExecutionProcessEnum.SUCCESS, "Execution is running");
        		return true;
        	}
            else {
                PersistentExecutionManager.removeExecution(executionCode, false);
                ServerMessageSender.reportExecutionState(executionCode, ExecutionProcessEnum.FAIL, "Network error, execution initial ping doesn't respond");
            }
		} catch (Exception e) {
			PersistentExecutionManager.removeExecution(executionCode, false);
			e.printStackTrace();
		}
        return false;
    }


    /**
     * Pings a given address
     * @param vmIP The IP to be pinged
     * @return True if the given IP responds ping, false otherwise
     */
    private boolean pingVerification(String vmIP) {
        try {
        	Process p = Runtime.getRuntime().exec(OSFactory.getOS().getPingCommand(vmIP));
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for (String h; (h = br.readLine()) != null;) {
                if (h.toUpperCase().contains("TTL")) {
                    p.destroy();
                    br.close();
                    return true;
                }
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return false;
    }


}
