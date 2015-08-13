package hypervisorManager;

import java.util.HashMap;
import java.util.Map;

import com.losandes.utils.VariableManager;

/**
 * Factory responsible for managing hypervisor classes and instances. This class provides methods to dinamically load hypervisor clasees and instantiate them.
 * @author Clouder
 */
public class HypervisorFactory {
    /**
     * All provide services must be statically accessed
     */
    private HypervisorFactory() {
    }
    /**
     * Map that contains a relation between hypervisor names and hypervisor objects
     */
    private static Map<String,Hypervisor> map = new HashMap<>();
    
    public static void registerHypervisors(){
    	String vmRun=VariableManager.local.getStringValue("VMRUN_PATH");
    	String vBox=VariableManager.local.getStringValue("VBOX_PATH");
    	if(vmRun!=null)map.put(VMwareWorkstation.HYPERVISOR_ID,new VMwareWorkstation(vmRun));
    	if(vBox!=null)map.put(VirtualBox.HYPERVISOR_ID,new VirtualBox(vBox));
    	//TODO add support to vmWarePlayer
    	//map.put(VMwarePlayer.HYPERVISOR_ID,new VMwarePlayer(VariableManager.local.getsetStringValue("VMRUN_PATH","C:\\Program Files (x86)\\VMware\\VMware VIX\\vmrun.exe")));
    }

    /**
     * Uses the map to search for hypervisor instances, if there is not an entry for the given name then it is loaded dinamically using java´s reflection API. If there is an associated object, then a new instance is returned by using the method getInstance from Hypervisor abstract class.
     * @param hypervisorName The hypervisor name to be instantiated
     * @param executablePath The executable route that represents the given hypervisor.
     * @param virtualMachinePath The virtual machine path to be associated with the hypervisor.
     * @return A managed hypervisor for the given name
     */
    public static Hypervisor getHypervisor(final String hypervisorId){
    	return map.get(hypervisorId);
    }
}