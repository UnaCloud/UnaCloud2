package uniandes.unacloud.agent.hypervisor;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.VirtualMachineExecution;

/**
 * Abstract class to be implemented by each hypervisor. It must be only instantiated by the hypervisor factory
 * @author Clouder
 */
public abstract class Hypervisor {

    /**
     * Path to this hypervisor executable
     */
    private String executablePath;
    
    public Hypervisor(String path){
    	this.executablePath=path;
    }

    public String getExecutablePath() {
        return executablePath;
    }

    /**
     * Stars a image
     * @param image to be started
     * @throws HypervisorOperationException
     */
    public abstract void startVirtualMachine(ImageCopy image) throws HypervisorOperationException;
    
    public abstract void configureVirtualMachineHardware(int cores,int ram,ImageCopy image) throws HypervisorOperationException;
    
    /**
     * turns off the managed virtual machine
     * @param image to be stopped
     */
    public abstract void stopVirtualMachine(ImageCopy image);

    
    /**
     * Restarts the managed virtual machine
     * @param image  virtual machine will be restarted
     * @throws HypervisorOperationException If there is an error restating the virtual machine
     */ 
    public abstract void restartVirtualMachine(ImageCopy image) throws HypervisorOperationException;

   
    /**
     * Executes a command on the managed virtual machine
     * @param image where will be execute command
     * @param command to be executed
     * @param args
     * @throws HypervisorOperationException
     */
    public abstract void executeCommandOnMachine(ImageCopy image, String command,String...args) throws HypervisorOperationException;

    public abstract void takeVirtualMachineSnapshot(ImageCopy image,String snapshotname) throws HypervisorOperationException;

    public abstract void deleteVirtualMachineSnapshot(ImageCopy image,String snapshotname) throws HypervisorOperationException;
    
    public abstract void restoreVirtualMachineSnapshot(ImageCopy image,String snapshotname)throws HypervisorOperationException;
    
    public abstract boolean existsVirtualMachineSnapshot(ImageCopy image,String snapshotname)throws HypervisorOperationException;
   
    /**
     * writes a file on the virtual machine file system
     * @param image where will be copied file
     * @param destinationRoute the route on the virtual machine file system where the file is going to be written
     * @param sourceFile file that will be copied
     * @throws HypervisorOperationException
     */
    public abstract void copyFileOnVirtualMachine(ImageCopy image,String destinationRoute, File sourceFile) throws HypervisorOperationException;
    
    public abstract void changeVirtualMachineMac(ImageCopy image) throws HypervisorOperationException;
    
    public abstract void registerVirtualMachine(ImageCopy image);
    public abstract void unregisterVirtualMachine(ImageCopy image);
    public abstract void cloneVirtualMachine(ImageCopy source,ImageCopy dest);
    public void stopAndUnregister(ImageCopy image){
    	synchronized (image) {
    		stopVirtualMachine(image);
        	unregisterVirtualMachine(image);
        	ImageCacheManager.freeLockedImageCopy(image);
		}
    }
    
    void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException ex) {
            Logger.getLogger(VirtualBox.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * Used to validate if a list of execution are running in hypervisor.
     * @param executions
     * @return List of execution that are not running in hypervisor
     */
	public abstract List<VirtualMachineExecution> checkExecutions(Collection<VirtualMachineExecution> executions);
}
