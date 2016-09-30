package uniandes.unacloud.agent.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Factory responsible for managing hypervisor classes and instances. This class provides methods to dynamically load hypervisor classes and instantiate them.
 * @author Clouder
 * @author CesarF
 */
public class PlatformFactory {
	
    /**
     * All provide services must be statically accessed
     */
    private PlatformFactory() {
    }
    
    /**
     * Map that contains a relation between hypervisor names and hypervisor objects
     */
    private static Map<String,Platform> map = new HashMap<>();
    
    public static void registerHypervisors(){
    	String vmRun=VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.VMRUN_PATH);
    	String vBox=VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.VBOX_PATH);
    	if(vmRun!=null)map.put(VMwareWorkstation.HYPERVISOR_ID,new VMwareWorkstation(vmRun));
    	if(vBox!=null)map.put(VirtualBox.HYPERVISOR_ID,new VirtualBox(vBox));
    	//TODO add support to vmWarePlayer
    	//map.put(VMwarePlayer.HYPERVISOR_ID,new VMwarePlayer(VariableManager.local.getsetStringValue("VMRUN_PATH","C:\\Program Files (x86)\\VMware\\VMware VIX\\vmrun.exe")));

    }
  
    /**
     * Uses the map to search for hypervisor instances, if there is not an entry for the given name then it is loaded dynamically using java's reflection API. If there is an associated object, 
     * then a new instance is returned by using the method getInstance from Hypervisor abstract class.
     * @param hypervisorId The hypervisor id to be instantiated
     * @return A managed hypervisor for the given name
     */
    public static Platform getHypervisor(final String hypervisorId){
    	return map.get(hypervisorId);
    }
    
    /**
     * Validates the list of executions in each hypervisor, returns the list of executions that are not running in any hypervisor
     * @return list of executions that are not running
     */
    public static List<Execution> validateExecutions(Collection<Execution> executions){
    	List<Execution> notRunningExecutions = new ArrayList<Execution>();
    	notRunningExecutions.addAll(executions);
    	for(Platform hypervisor:map.values())notRunningExecutions=hypervisor.checkExecutions(notRunningExecutions);
    	return notRunningExecutions;
    }
}