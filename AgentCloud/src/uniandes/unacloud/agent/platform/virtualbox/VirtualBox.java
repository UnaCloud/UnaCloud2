package uniandes.unacloud.agent.platform.virtualbox;

import static uniandes.unacloud.common.utils.UnaCloudConstants.ERROR_MESSAGE;

import java.io.File;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.exceptions.UnsupportedPlatformException;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.host.system.OSFactory;
import uniandes.unacloud.agent.platform.Platform;
import uniandes.unacloud.agent.utils.AddressUtility;
import uniandes.unacloud.utils.LocalProcessExecutor;

/**
 * Implementation of platform abstract class to give support for VirtualBox
 * platform.
 */
public abstract class VirtualBox extends Platform {
	
	private static final String HEADLESS_SERVICE_NAME = "VBoxHeadless";
	
	private static final String VBOX_SERVICE_NAME = "VBoxSVC";
	    
	/**
	 * Class constructor
	 * @param path Path to this platform executable
	 * @throws UnsupportedPlatformException 
	 */
	public VirtualBox(String path) {		
		super(path);
	}
   	
    /**
     * Sends a stop command to the platform
     * @param image Image copy to be stopped 
     */
    @Override
    public void stopExecution(ImageCopy image){
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "controlvm", image.getImageName(), "poweroff");
        sleep(2000);
    }
    
    /**
     * Registers a virtual machine on the platform
     * @param image Image copy to be registered
     */
    @Override
	public void registerImage(ImageCopy image){
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "registervm", image.getMainFile().getExecutableFile().getPath());
        sleep(15000);
    }
    
    /**
     * Unregisters a virtual machine from the platform
     * @param image Image copy to be unregistered
     */
    @Override
	public void unregisterImage(ImageCopy image){
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "unregistervm", image.getImageName());
        sleep(15000);
    }
    
    /**
     * Sends a reset message to the platform
     * @param image Image to be restarted
     */
    @Override
    public void restartExecution(ImageCopy image) throws PlatformOperationException {
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "controlvm", image.getImageName(), "reset");
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(30000);
    }
    
    /**
     * Sends a start message to the platform
     * @param image Image to be started
     */
    @Override
	public void startExecution(ImageCopy image) throws PlatformOperationException {
		setPriority(image);
        String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "startvm", image.getImageName(), "--type", "headless");
        if (h.contains(ERROR_MESSAGE)) {
            throw new PlatformOperationException(h.length() < 100 ? h : h.substring(0, 100));
        }
        sleep(30000);
        try {
        	OSFactory.getOS().setPriorityProcess(HEADLESS_SERVICE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
        sleep(1000);
    }

	private void setPriority(ImageCopy image) throws PlatformOperationException {
		//To correct executions in Vbox 4.3 and forward
    	try {
    		LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "showvminfo", image.getImageName());
    		sleep(1000);
    		OSFactory.getOS().setPriorityProcess(VBOX_SERVICE_NAME);
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
    public void configureExecutionHardware(int cores, int ram, ImageCopy image) throws PlatformOperationException {
    	if (cores != 0 && ram != 0) {
            LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "modifyvm", image.getImageName(), "--memory", ""+ram, "--cpus", ""+cores);
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
    public void executeCommandOnExecution(ImageCopy image,String command, String... args) throws PlatformOperationException {
        List<String> com = new ArrayList<>();
        Collections.addAll(com, createExecutionCommand(getExecutablePath(), image.getImageName(), command, image.getImage().getUsername(), image.getImage().getPassword()));
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
    public void copyFileOnExecution(ImageCopy image, String destinationRoute, File sourceFile) throws PlatformOperationException {
       	String h = LocalProcessExecutor.executeCommandOutput(createCopyToCommand(getExecutablePath(), image.getImageName(), sourceFile.getAbsolutePath(), destinationRoute, image.getImage().getUsername(), image.getImage().getPassword()));
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
    public void takeExecutionSnapshot(ImageCopy image,String snapshotname) {
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "snapshot", image.getImageName(), "take", snapshotname);
        sleep(20000);
    }
    
    /**
     * Deletes a snapshot of the VM
     * @param image copy of the image to delete its snapshot
     * @param snapshotname 
     */
    @Override
    public void deleteExecutionSnapshot(ImageCopy image,String snapshotname) {
        LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "snapshot", image.getImageName(), "delete", snapshotname);
        sleep(20000);
    }
    
    /**
     * Changes the VM MAC address
     * @param image copy to be modified
     */
    @Override
    public void changeExecutionMac(ImageCopy image) throws PlatformOperationException {
    	NetworkInterface ninterface = AddressUtility.getDefaultNetworkInterface();
    	LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "modifyvm", image.getImageName(), "--bridgeadapter1", ninterface.getDisplayName(), "--macaddress1", "auto");
        sleep(20000);        
    }

    /**
     * Restores a VM to its snapshot
     * @param image copy to be reverted
     * @param snapshotname snapshot to which image will be restored
     */
	@Override
	public void restoreExecutionSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "snapshot", image.getImageName(), "restorecurrent");
        sleep(20000);
	}
	
	/**
	 * Verifies if the VM has the specified snapshot
	 * @param image 
	 * @param snapshotname 
	 */
	@Override
	public boolean existsExecutionSnapshot(ImageCopy image, String snapshotname) throws PlatformOperationException {
		String h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "snapshot", image.getImageName(), "list");
        sleep(20000);
        return h != null && !h.contains("does not");
	}
	
	/**
	 * Unregisters all VMs from platform
	 */
	public void unregisterAllVms(){
		String[] h = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "list", "vms").split("\n|\r");
		for (String vm : h) {
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
	public void cloneImage(ImageCopy source, ImageCopy dest) {
		LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "clonevm", source.getImageName(), "--snapshot", "unacloudbase", "--name", dest.getImageName(), "--basefolder", dest.getMainFile().getExecutableFile().getParentFile().getParentFile().getAbsolutePath(), "--register");
		sleep(20000);
		takeExecutionSnapshot(dest, "unacloudbase");
        unregisterImage(dest);
	}
	
	@Override
	public List<Execution> checkExecutions(Collection<Execution> executions) {
		List<Execution> executionsToDelete = new ArrayList<Execution>();
		List<String> list = new ArrayList<String>();
		try {
			String[] result = LocalProcessExecutor.executeCommandOutput(getExecutablePath(), "list", "runningvms").split("\n|\r");
			for (String vm : result) {
				list.add(vm.split(" ")[0].replace("\"", "").trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Execution execution: executions) {
			boolean isRunning = false;
			for (String exeInplatform : list) {
				if (exeInplatform.contains(execution.getImage().getImageName())) {
					isRunning = true;
					break;
				}
			}	
			if (!isRunning)
				executionsToDelete.add(execution);	
		}
		return executionsToDelete;
	}
		
	/**
	 * Method to create command to be executed in guest machine
	 * @param path : VBoxManage path
	 * @param imageName : image name
	 * @param command : command to be executed in guest
	 * @param username : username in virtual machine
	 * @param password : password for username
	 * @return Array with all command elements
	 */
	public abstract String[] createExecutionCommand(String path, String imageName, String command, String username, String password);
	
	/**
	 * Method to create command to copy files in guest machine
	 * @param path : VBoxManage path
	 * @param imageName : image name
	 * @param sourcePath : file path to be copied in guest
	 * @param guestPath : file path to be replaced in guest
	 * @param username : username in virtual machine
	 * @param password : password for username
	 * @return Array with all command elements
	 */
	public abstract String[] createCopyToCommand(String path, String imageName, String sourcePath, String guestPath, String username, String password);
}