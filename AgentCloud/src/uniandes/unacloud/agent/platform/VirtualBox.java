package uniandes.unacloud.agent.platform;

import static uniandes.unacloud.common.utils.UnaCloudConstants.ERROR_MESSAGE;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.agent.system.OSFactory;
import uniandes.unacloud.agent.utils.AddressUtility;
import uniandes.unacloud.common.utils.LocalProcessExecutor;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Implementation of hypervisor abstract class to give support for VirtualBox
 * hypervisor.
 */
public class VirtualBox extends Platform {
	public static final String HYPERVISOR_ID=UnaCloudConstants.VIRTUAL_BOX;
    
	/**
	 * Class constructor
	 * @param path Path to this hypervisor executable
	 */
	public VirtualBox(String path) {
		super(path);
	}
    
    /**
     * Sends a stop command to the hypervisor
     * @param image Image copy to be stopped 
     */
    @Override
    public void stopVirtualMachine(ImageCopy image){
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "controlvm", image.getVirtualMachineName(), "poweroff");
        sleep(2000);
    }
    
    /**
     * Registers a virtual machine on the hypervisor
     * @param image Image copy to be registered
     */
    @Override
	public void registerVirtualMachine(ImageCopy image){
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "registervm", image.getMainFile().getPath());
        sleep(15000);
    }
    
    /**
     * Unregisters a virtual machine from the hypervisor
     * @param image Image copy to be unregistered
     */
    @Override
	public void unregisterVirtualMachine(ImageCopy image){
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "unregistervm", image.getVirtualMachineName());
        sleep(15000);
    }
    
    /**
     * Sends a reset message to the hypervisor
     * @param image Image to be restarted
     */
    @Override
    public void restartVirtualMachine(ImageCopy image) throws PlatformOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "controlvm", image.getVirtualMachineName(), "reset");
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(30000);
    }
    
    /**
     * Sends a start message to the hypervisor
     * @param image Image to be started
     */
    @Override
	public void startVirtualMachine(ImageCopy image) throws PlatformOperationException {
		setPriority(image);
        String h;
        if((h=LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "startvm", image.getVirtualMachineName(), "--type", "headless")).contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(30000);
        try {
			LocalProcessExecutor.executeCommandOutput(OSFactory.getOS().getSetPriorityCommand("VBoxHeadless.exe"));
		} catch (Exception e) {
			e.printStackTrace();
		}
        sleep(1000);
    }

	private void setPriority(ImageCopy image) throws PlatformOperationException {
		//To correct executions in Vbox 4.3 and forward
    	try {
    		LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"showvminfo",image.getVirtualMachineName());
    		sleep(1000);
    		LocalProcessExecutor.executeCommandOutput(OSFactory.getOS().getSetPriorityCommand("VBoxSVC.exe"));
    		sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * Changes VM configuration
     * @param cores new number of cores for the VM
     * @param ram new RAM value for the VM
     * @param image Copy to be modified
     */
    @Override
    public void configureVirtualMachineHardware(int cores, int ram, ImageCopy image) throws PlatformOperationException {
    	if(cores!=0&&ram!=0){
            LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "modifyvm", image.getVirtualMachineName(),"--memory",""+ram,"--cpus",""+cores);
            sleep(20000);
        }
    }
    
    /**
     * Executes a command to the VM itself
     * @param image copy in which command will be executed
     * @param command command to be executed
     * @param args command arguments 
     */
    @Override
    public void executeCommandOnMachine(ImageCopy image,String command, String... args) throws PlatformOperationException {
        List<String> com = new ArrayList<>();
        Collections.addAll(com, getExecutablePath(), "--nologo", "guestcontrol", image.getVirtualMachineName(), "execute", "--image", command, "--username", image.getImage().getUsername(), "--password", image.getImage().getPassword(), "--wait-exit", "--");
        Collections.addAll(com, args);
        String h = LocalProcessExecutor.executeCommandOutput(com.toArray(new String[0]));
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(10000);
    }
    /**
     * Sends a file to the VM itself
     * @param image copy in which file will be pasted
     * @param destinationRoute route in which file will be pasted
     * @param sourceFile file to be copied
     */
    @Override
    public void copyFileOnVirtualMachine(ImageCopy image, String destinationRoute, File sourceFile) throws PlatformOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "guestcontrol", image.getVirtualMachineName(), "copyto", sourceFile.getAbsolutePath(), destinationRoute, "--username", image.getImage().getUsername(), "--password", image.getImage().getPassword());
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(10000);
    }
    
    /**
     * Takes a snapshot of the VM
     * @param image copy of the image that will have the new snapshot
     * @param snapshotname 
     */
    @Override
    public void takeVirtualMachineSnapshot(ImageCopy image,String snapshotname){
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"snapshot",image.getVirtualMachineName(),"take",snapshotname);
        sleep(20000);
    }
    
    /**
     * Deletes a snapshot of the VM
     * @param image copy of the image to delete its snapshot
     * @param snapshotname 
     */
    @Override
    public void deleteVirtualMachineSnapshot(ImageCopy image,String snapshotname){
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"snapshot",image.getVirtualMachineName(),"delete",snapshotname);
        sleep(20000);
    }
    
    /**
     * Changes the VM MAC address
     * @param image copy to be modified
     */
    @Override
    public void changeVirtualMachineMac(ImageCopy image) throws PlatformOperationException {
    	NetworkInterface ninterface=AddressUtility.getDefaultNetworkInterface();
    	LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "modifyvm", image.getVirtualMachineName(),"--bridgeadapter1",ninterface.getDisplayName(),"--macaddress1","auto");
        sleep(20000);
        
    }

    /**
     * Restores a VM to its snapshot
     * @param image copy to be reverted
     * @param snapshotname snapshot to which image will be restored
     */
	@Override
	public void restoreVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "snapshot", image.getVirtualMachineName(), "restorecurrent");
        sleep(20000);
	}
	
	/**
	 * Verifies if the VM has the specified snapshot
	 * @param image 
	 * @param snapshotname 
	 */
	@Override
	public boolean existsVirtualMachineSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
		String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "snapshot", image.getVirtualMachineName(), "list");
        sleep(20000);
        return h != null && !h.contains("does not");
	}
	
	/**
	 * Unregisters all VMs from hypervisor
	 */
	public void unregisterAllVms(){
		String[] h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "list","vms").split("\n|\r");
		for(String vm:h){
			LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "unregistervm", vm.split(" ")[1]);
	        sleep(15000);
		}
	}
	
	/**
	 * Clones an image making a new copy
	 * @param source source copy
	 * @param dest empty destination copy
	 */
	@Override
	public void cloneVirtualMachine(ImageCopy source, ImageCopy dest) {
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(),"clonevm",source.getVirtualMachineName(),"--snapshot","unacloudbase","--name",dest.getVirtualMachineName(),"--basefolder",dest.getMainFile().getParentFile().getParentFile().getAbsolutePath(),"--register");
		sleep(20000);
		takeVirtualMachineSnapshot(dest,"unacloudbase");
        unregisterVirtualMachine(dest);
	}
	
	@Override
	public List<Execution> checkExecutions(Collection<Execution> executions) {
		List<Execution> executionsToDelete = new ArrayList<Execution>();
		List<String> list = new ArrayList<String>();
		try {
			String[] result = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "list","runningvms").split("\n|\r");
			for(String vm: result){
				list.add(vm.split(" ")[0].replace("\"", "").trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Execution execution: executions) {
			boolean isRunning = false;
			for(String exeInHypervisor: list){
				if(exeInHypervisor.contains(execution.getImage().getVirtualMachineName())){
					isRunning = true;
					break;
				}
			}	
			if(!isRunning)executionsToDelete.add(execution);	
		}
		return executionsToDelete;
	}
}