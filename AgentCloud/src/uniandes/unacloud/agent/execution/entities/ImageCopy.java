package uniandes.unacloud.agent.execution.entities;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.PersistentExecutionManager;
import uniandes.unacloud.agent.execution.configuration.AbstractExecutionConfigurator;
import uniandes.unacloud.agent.platform.Platform;
import uniandes.unacloud.agent.platform.PlatformFactory;
import uniandes.unacloud.agent.platform.PlatformOperationException;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Represents a copy from an image. Image files ares copied too
 *
 * @author CesarF
 *
 */
public class ImageCopy implements Serializable{
	
	private static final long serialVersionUID = 8911955514393569155L;
	
	/**
	 * executable file name
	 */
	private File mainFile;
	
	/**
	 * Original image
	 */
	private Image image;
	
	private transient ImageStatus status=ImageStatus.FREE;
	
	/**
	 * Holds the platform specific execution ID
	 */
	private String platformExecutionID;
	
	/**
	 * Get main file
	 */
	public File getMainFile() {
		return mainFile;
	}
	/**
	 * Update main file
	 * @param mainFile
	 */
	public void setMainFile(File mainFile) {
		this.mainFile = mainFile;
	}
	/**
	 * Gets image name
	 * @return
	 */
	public String getImageName() {
		if(mainFile==null)return "null";
		String h=mainFile.getName();
		int l=h.lastIndexOf(".");
		if(l==-1)return h;
		return h.substring(0,l);
	}
	/**
	 * Gets image status
	 * @return status
	 */
	public ImageStatus getStatus() {
		return status;
	}
	/**
	 * Updates image status
	 * @param status
	 */
	public void setStatus(ImageStatus status) {
		this.status = status;
	}
	/**
	 * Returns image
	 * @return
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Updates image
	 * @param image
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * Returns the platform's execution ID
	 * @return
	 */
	public String getPlatformExecutionID() {
		return platformExecutionID;
	}
	
	/**
	 * Updates the platofrm's execution ID
	 * @param executionID
	 */
	public void setPlatformExecutionID(String executionID) {
		this.platformExecutionID = executionID;
	}
	
	/**
	 * Configures and starts the copy
	 * @param machineExecution
	 */	
	public synchronized void configureAndStart(Execution machineExecution){
		Platform platform=PlatformFactory.getPlatform(getImage().getPlatformId());
		try {
			try {
				if(platform==null)throw new Exception("platform not found "+getImage().getPlatformId());
				if(status!=ImageStatus.STARTING)status=ImageStatus.STARTING;
				Class<?> configuratorClass=Class.forName("uniandes.unacloud.agent.execution.configuration."+getImage().getConfiguratorClass());
				Object configuratorObject=configuratorClass.getConstructor().newInstance();
				
				if(configuratorObject instanceof AbstractExecutionConfigurator){
					AbstractExecutionConfigurator configurator=(AbstractExecutionConfigurator)configuratorObject;
					//configurator.setplatform(platform);
					configurator.setExecution(machineExecution);
					//TODO Evaluar si hacerlo en el apagado porque es mas importante el tiempo de arranque.
					platform.registerImage(this);
	    			platform.restoreExecutionSnapshot(this,"unacloudbase");
	        		platform.configureExecutionHardware(machineExecution.getCores(),machineExecution.getMemory(),this);
	    			platform.startExecution(this);
	    			configurator.configureHostname();
	    			configurator.configureIP();
	    			System.out.println("image config "+new Date());
	    	        PersistentExecutionManager.startUpMachine(machineExecution,!configurator.doPostConfigure());	    	       
				}else {
					ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionStateEnum.FAILED,"Invalid execution configurator.");
					status=ImageStatus.FREE;
				}
				
			} catch (Exception e) {
				e.printStackTrace(System.out);
				ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionStateEnum.FAILED,"Configurator class error: "+e.getMessage());
				status=ImageStatus.FREE;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Clones the copy 
	 * @param dest empty image copy
	 * @return cloned image
	 */
	public synchronized ImageCopy cloneCopy(ImageCopy dest){
		Platform platform=PlatformFactory.getPlatform(this.getImage().getPlatformId());
		platform.cloneImage(this,dest);
		return dest;
	}
	
	/**
	 * Makes initialization process in copy before starting
	 * @throws ExecutionException
	 */
	public synchronized void init()throws ExecutionException{
		Platform platform=PlatformFactory.getPlatform(image.getPlatformId());
		if(platform==null)throw new ExecutionException("Platform doesn't exists on machine. platform was "+image.getPlatformId());
		platform.registerImage(this);
		try {
			platform.changeExecutionMac(this);
			platform.takeExecutionSnapshot(this,UnaCloudConstants.DEFAULT_IMG_NAME);
		} catch (PlatformOperationException e) {
			e.printStackTrace();
		}
		platform.unregisterImage(this);
	}
	
	/**
	 * Delete the snapshot of this image to save it with current state and files
	 * @throws ExecutionException
	 * @throws PlatformOperationException 
	 */
	public synchronized void deleteSnapshot()throws ExecutionException, PlatformOperationException{
		Platform platform=PlatformFactory.getPlatform(this.getImage().getPlatformId());
		platform.deleteExecutionSnapshot(this,UnaCloudConstants.DEFAULT_IMG_NAME);		
	}

	
	/**
	 * Starts copy
	 * @throws PlatformOperationException
	 */
	public void startExecution()throws PlatformOperationException{ 
		Platform platform=PlatformFactory.getPlatform(this.getImage().getPlatformId());
		platform.startExecution(this);
	}
    
	/**
	 * Executes given command on this copy
	 * @param command command to get executed
	 * @param args command arguments
	 * @throws PlatformOperationException
	 */
    public void executeCommandOnExecution( String command,String...args) throws PlatformOperationException{
    	Platform platform=PlatformFactory.getPlatform(image.getPlatformId());
    	platform.executeCommandOnExecution(this,command,args);
    }
    
    /**
     * copies a file in this image clone
     * @param destinationRoute destination path in the VM
     * @param sourceFile original file
     * @throws PlatformOperationException
     */
    public void copyFileOnExecution(String destinationRoute, File sourceFile) throws PlatformOperationException{
    	Platform platform=PlatformFactory.getPlatform(image.getPlatformId());
    	platform.copyFileOnExecution(this,destinationRoute,sourceFile);
    }
    
    /**
     * Restarts this VM
     * @throws PlatformOperationException
     */
    public void restartExecution() throws PlatformOperationException{
    	Platform platform=PlatformFactory.getPlatform(this.getImage().getPlatformId());
		platform.restartExecution(this);
    }
    
    /**
     * Finalizes copy execution
     */
    public synchronized void stopAndUnregister(){
    	Platform platform=PlatformFactory.getPlatform(image.getPlatformId());
    	platform.stopAndUnregister(this);
    }
    /**
     * Finalizes copy execution without unregistering and freeing image
     */
    public synchronized void stop(){
    	Platform platform=PlatformFactory.getPlatform(image.getPlatformId());
    	platform.stopExecution(this);
    }
    /**
     * Unregistering image copy
     */
    public synchronized void unregister(){
    	Platform platform=PlatformFactory.getPlatform(image.getPlatformId());
    	platform.unregisterImage(this);
    }
}