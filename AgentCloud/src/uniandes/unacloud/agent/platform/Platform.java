package uniandes.unacloud.agent.platform;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

/**
 * Abstract class to be implemented by each platform. It must be only instantiated by the platform factory
 * @author Clouder
 */
public abstract class Platform {
	
	/**
	 * Represents unique code for platform
	 */
	protected String code;

    /**
     * Path to this platform executable
     */
    private String executablePath;
    
    public Platform(String path){
    	this.executablePath=path;
    }

    public String getExecutablePath() {
        return executablePath;
    }

    /**
     * Stars a image
     * @param image to be started
     * @throws PlatformOperationException
     */
    public abstract void startExecution(ImageCopy image) throws PlatformOperationException;
    
    /**
     * Configures hardware for Image
     * @param cores
     * @param ram
     * @param image
     * @throws PlatformOperationException
     */
    public abstract void configureExecutionHardware(int cores,int ram,ImageCopy image) throws PlatformOperationException;
    
    /**
     * turns off the managed execution
     * @param image to be stopped
     */
    public abstract void stopExecution(ImageCopy image);

    
    /**
     * Restarts the managed execution
     * @param image execution will be restarted
     * @throws PlatformOperationException If there is an error restarting the execution
     */ 
    public abstract void restartExecution(ImageCopy image) throws PlatformOperationException;

   
    /**
     * Executes a command on the managed execution
     * @param image where will be execute command
     * @param command to be executed
     * @param args
     * @throws PlatformOperationException
     */
    public abstract void executeCommandOnExecution(ImageCopy image, String command,String...args) throws PlatformOperationException;

    public abstract void takeExecutionSnapshot(ImageCopy image,String snapshotname) throws PlatformOperationException;

    public abstract void deleteExecutionSnapshot(ImageCopy image,String snapshotname) throws PlatformOperationException;
    
    public abstract void restoreExecutionSnapshot(ImageCopy image,String snapshotname)throws PlatformOperationException;
    
    public abstract boolean existsExecutionSnapshot(ImageCopy image,String snapshotname)throws PlatformOperationException;
   
    /**
     * writes a file on the execution file system
     * @param image where will be copied file
     * @param destinationRoute the route on the execution file system where the file is going to be written
     * @param sourceFile file that will be copied
     * @throws PlatformOperationException
     */
    public abstract void copyFileOnExecution(ImageCopy image,String destinationRoute, File sourceFile) throws PlatformOperationException;
    
    public abstract void changeExecutionMac(ImageCopy image) throws PlatformOperationException;
    
    public abstract void registerImage(ImageCopy image);
    
    public abstract void unregisterImage(ImageCopy image);
    
    public abstract void cloneImage(ImageCopy source,ImageCopy dest);
    
    public void stopAndUnregister(ImageCopy image){
    	synchronized (image) {
    		stopExecution(image);
        	unregisterImage(image);
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
     * Used to validate if a list of execution are running in platform.
     * @param executions
     * @return List of execution that are not running in platform
     */
	public abstract List<Execution> checkExecutions(Collection<Execution> executions);
	
	/**
	 * Validates that an execution has correctly started. The default behavior 
	 * is doing a ping with retries with the execution's ip.  
	 * @param execution: the execution to test
	 * @returncorrectly
	 */
	public boolean checkExecutionStarted(Execution execution) {
		String execIP = execution.getMainInterface().getIp();
    	System.out.println("Start checking by ip to "+execIP);
    	
        boolean red=false;
        for(int e=0;e<8 && !red;e++){
            if(!(red=pingVerification(execIP)))try{Thread.sleep(30000);}catch(Exception ex){}
        }
        return red;
	}
	

    /**
     * Pings a given address
     * @param vmIP The IP to be pinged
     * @return True if the given IP responds ping, false otherwise
     */
    private boolean pingVerification(String vmIP){
        try {
        	// TODO: make ping cross-platform
            Process p = Runtime.getRuntime().exec("ping -c 2 " + vmIP);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            for(String h;(h=br.readLine())!=null;){
                if(h.toUpperCase().contains("TTL")){
                    p.destroy();
                    br.close();
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Platform.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
	/**
	 * Returns code to recognize king of platform
	 * @return code
	 */
	public String getCode() {
		if(code == null)code = this.getClass().getSimpleName();
		return code;
	}
}
