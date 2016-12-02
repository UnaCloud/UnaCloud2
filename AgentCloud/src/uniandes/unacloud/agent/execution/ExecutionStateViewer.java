package uniandes.unacloud.agent.execution;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;






import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

/**
 * This class is responsible for checking if an execution has been correctly deployed. That is, if the execution has started and if it has well configured its IP address
 * @author Clouder
 */
public class ExecutionStateViewer {
	
	private long executionCode;
	private String vmIP;
	
	/**
	 * constructs an ExecutionStateViewer for the given execution.
	 * @param executionCode The execution id to make the execution status report to the server
     * @param vmIP The IP address to check if the execution is accessible
	 */
	public ExecutionStateViewer(long executionCode,String vmIP){
		this.executionCode=executionCode;
		this.vmIP=vmIP;
	}

    /**
     *  On this method the execution is checked and the state is reported to UnaCloud server
     */
    public boolean check(){
    	System.out.println("Start checking by ip to "+vmIP);
        boolean red=false;
        for(int e=0;e<8&&!red;e++){
            if(!(red=pingVerification(vmIP)))try{Thread.sleep(30000);}catch(Exception ex){}
        }
        try {
        	if(red){
        		ServerMessageSender.reportExecutionState(executionCode,ExecutionStateEnum.DEPLOYED,"Execution is running");
        		return true;
        	}
            else{
                PersistentExecutionManager.removeExecution(executionCode,false);
                ServerMessageSender.reportExecutionState(executionCode,ExecutionStateEnum.FAILED,"Network error, execution initial ping doesn't respond");
            }
		} catch (Exception e) {
			PersistentExecutionManager.removeExecution(executionCode,false);
			e.printStackTrace();
		}
        return false;
    }


    /**
     * Pings a given address
     * @param vmIP The IP to be pinged
     * @return True if the given IP responds ping, false otherwise
     */
    private boolean pingVerification(String vmIP){
        try {
            Process p = Runtime.getRuntime().exec("ping " + vmIP + " -n 2");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for(String h;(h=br.readLine())!=null;){
                if(h.contains("TTL")){
                    p.destroy();
                    br.close();
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ExecutionStateViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


}
