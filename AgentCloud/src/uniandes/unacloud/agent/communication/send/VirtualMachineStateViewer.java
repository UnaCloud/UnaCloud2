package uniandes.unacloud.agent.communication.send;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;





import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

/**
 * This class is responsible for checking if a virtual machine has been correctly deployed. That is, if the virtual machines has started and if it has well configured its IP address
 * @author Clouder
 */
public class VirtualMachineStateViewer {
	
	private long virtualMachineCode;
	private String vmIP;
	
	/**
	 * constructs a VirtualMachineStateViewer for the given virtual machine.
	 * @param virtualMachineCode The virtual machine id to make the virtual machine status report to the server
     * @param vmIP The IP address to check if the virtual machine is accessible
	 */
	public VirtualMachineStateViewer(long virtualMachineCode,String vmIP){
		this.virtualMachineCode=virtualMachineCode;
		this.vmIP=vmIP;
	}

    /**
     *  On this method the virtual machine is checked and the state is reported to UnaCloud server
     */
    public boolean check(){
    	System.out.println("Start checking by ip to "+vmIP);
        boolean red=false;
        for(int e=0;e<8&&!red;e++){
            if(!(red=pingVerification(vmIP)))try{Thread.sleep(30000);}catch(Exception ex){}
        }
        try {
        	if(red){
        		ServerMessageSender.reportVirtualMachineState(virtualMachineCode,ExecutionStateEnum.DEPLOYED,"Machine started");
        		return true;
        	}
            else{
                PersistentExecutionManager.removeExecution(virtualMachineCode,false);
                ServerMessageSender.reportVirtualMachineState(virtualMachineCode,ExecutionStateEnum.FAILED,"Network error, machine initial ping doesn't respond");
            }
		} catch (Exception e) {
			PersistentExecutionManager.removeExecution(virtualMachineCode,false);
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
            Logger.getLogger(VirtualMachineStateViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


}
