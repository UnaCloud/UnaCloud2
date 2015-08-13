package reporter;

import communication.ServerMessageSender;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import unacloudEnums.VirtualMachineExecutionStateEnum;
import virtualMachineManager.PersistentExecutionManager;

/**
 * This class is responsible for checking if a VMware virtual machine has been correctly deployed. That is, if the virtual machines has started and if it has well configured its IP address
 * @author Clouder
 */
public class VirtualMachineStateViewer {

    /**
     * constructs a VirtualMachineStateViewer for the given virtual machine. On this method the virtual machine is checked and the state is reported to UnaCloud server
     * @param virtualMachineCode The virtual machine id to make the virtual machine status report to the server
     * @param hypervisorPath The path of the hypervisor to be used to check the virtual machine status
     * @param vmIP The ip address to check if the virtual machine is accessible
     * @param vmPath The path of the virtual machine that must be checked
     * @param hypervisorName The type of hypervisor used to deploy the virtual machine
     */
    public VirtualMachineStateViewer(long virtualMachineCode,String vmIP, String message){
        boolean red=false;
        for(int e=0;e<8&&!red;e++){
            if(!(red=pingVerification(vmIP)))try{Thread.sleep(30000);}catch(Exception ex){}
        }
        if(red)ServerMessageSender.reportVirtualMachineState(virtualMachineCode,VirtualMachineExecutionStateEnum.DEPLOYED,"Machine started");
        else{
            PersistentExecutionManager.removeExecution(virtualMachineCode,false);
            ServerMessageSender.reportVirtualMachineState(virtualMachineCode,VirtualMachineExecutionStateEnum.FAILED,message);
        }
    }

    /**
     * This method uses the vmrun command to ask for a running virtual machine list, then each running virtual machine is compared with the given one and if there is at least one equal it returns true.
     * @param hypervisorPath The path of the hypervisor executable to ask for its virtual machine list
     * @param vmPath The path of the virtual machine to be checked
     * @return Returns true if the virtual machine is started by a vmware product, false otherwise
     */
    private boolean vmrunListVerification(String hypervisorPath,String vmPath){
        boolean ret = false;
        if(!hypervisorPath.endsWith("/")&&!hypervisorPath.endsWith("\\"))hypervisorPath=hypervisorPath+"/";
        try {
            Process p = new ProcessBuilder(hypervisorPath+"vmrun.exe","list").start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int e = Integer.parseInt(br.readLine().split(":")[1].trim()),i=0;
            File vmx = new File(vmPath);
            for(String h;i<e&&(h=br.readLine())!=null;i++){
                if(new File(h).equals(vmx)){
                    ret=true;
                    br.close();
                    p.destroy();
                    break;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(VirtualMachineStateViewer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    /**
     * Pings the given address
     * @param vmIP The ip to be pinged
     * @return True if the given ip responds ping, false otherwise
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
