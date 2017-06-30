package uniandes.unacloud.agent.platform;

import java.io.File;
import java.util.Collection;
import java.util.List;

import uniandes.unacloud.agent.exceptions.PlatformOperationException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.ImageCopy;

import uniandes.unacloud.agent.utils.SystemUtils;

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
    
    public Platform(String path) {
    	this.executablePath = path;
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
    
    protected void sleep(long milli) {
    	SystemUtils.sleep(milli);      
    }
    /**
     * Used to validate if a list of execution are running in platform.
     * @param executions
     * @return List of execution that are not running in platform
     */
	public abstract List<Execution> checkExecutions(Collection<Execution> executions);
	
	/**
	 * Returns code to recognize king of platform
	 * @return code
	 */
	public String getCode() {
		if(code == null)
			code = this.getClass().getSimpleName();
		return code;
	}
}
