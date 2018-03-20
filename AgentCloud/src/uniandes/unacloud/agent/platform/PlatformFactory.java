package uniandes.unacloud.agent.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uniandes.unacloud.agent.exceptions.UnsupportedPlatformException;

import uniandes.unacloud.agent.platform.vmware.VMwareWorkstation;
import uniandes.unacloud.agent.platform.virtualbox.VBoxFactory;
import uniandes.unacloud.agent.platform.virtualbox.VirtualBox;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.utils.VariableManager;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Factory responsible for managing platform classes and instances. This class provides methods to dynamically load platform classes and instantiate them.
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
     * Map that contains a relation between platform names and platform objects
     */
    private static Map<String, Platform> map = new HashMap<>();
    
    public static void registerplatforms() {
    	String vmRun = VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.VMRUN_PATH);
    	String vBox = VariableManager.getInstance().getLocal().getStringVariable(UnaCloudConstants.VBOX_PATH);
    	if (vmRun != null) {
    		VMwareWorkstation vmwork = new VMwareWorkstation(vmRun);
    		map.put(vmwork.getCode(),vmwork);
    	}
    	if (vBox != null) {
    		VirtualBox vbox;
			try {
				vbox = VBoxFactory.getInstalledVirtualBoxPlatform(vBox);
				map.put(vbox.getCode(),vbox);
			} catch (UnsupportedPlatformException e) {
				e.printStackTrace();
			}    		
    	}
    	
    	//TODO add support to vmWarePlayer
    	//map.put(VMwarePlayer.platform_ID,new VMwarePlayer(VariableManager.local.getsetStringValue("VMRUN_PATH","C:\\Program Files (x86)\\VMware\\VMware VIX\\vmrun.exe")));
    }
  
    /**
     * Uses the map to search for platform instances, if there is not an entry for the given name then it is loaded dynamically using java's reflection API. If there is an associated object, 
     * then a new instance is returned by using the method getInstance from platform abstract class.
     * @param platformId The platform id to be instantiated
     * @return A managed platform for the given name
     */
    public static Platform getPlatform(final String platformId) {
    	return map.get(platformId);
    }
    
    /**
     * Validates the list of executions in each platform, returns the list of executions that are not running in any platform
     * @return list of executions that are not running
     */
    public static List<Execution> validateExecutions(Collection<Execution> executions) {
    	List<Execution> notRunningExecutions = new ArrayList<Execution>();
    	notRunningExecutions.addAll(executions);
    	for (Platform platform : map.values()) 
    		notRunningExecutions = platform.checkExecutions(notRunningExecutions);
    	return notRunningExecutions;
    }
}