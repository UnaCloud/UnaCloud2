package uniandes.unacloud.agent.execution.entities;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.exceptions.VirtualMachineExecutionException;
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
	 * Get manin file
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
	 * Gets virtual machine name
	 * @return
	 */
	public String getVirtualMachineName() {
		if(mainFile==null)return "null";
		String h=mainFile.getName();
		int l=h.lastIndexOf(".");
		if(l==-1)return h;
		return h.substring(0,l);
	}
	/**
	 * Gets virtual machine status
	 * @return status
	 */
	public ImageStatus getStatus() {
		return status;
	}
	/**
	 * Update virtual machine status
	 * @param status
	 */
	public void setStatus(ImageStatus status) {
		this.status = status;
	}
	/**
	 * Return image
	 * @return
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Update images
	 * @param image
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	/**
	 * Configures and starts the copy
	 * @param machineExecution
	 */	
	public synchronized void configureAndStart(Execution machineExecution){
		Platform hypervisor=PlatformFactory.getHypervisor(getImage().getHypervisorId());
		try {
			try {
				if(hypervisor==null)throw new Exception("Hypervisor not found "+getImage().getHypervisorId());
				if(status!=ImageStatus.STARTING)status=ImageStatus.STARTING;
				Class<?> configuratorClass=Class.forName("virtualMachineManager.configuration."+getImage().getConfiguratorClass());
				Object configuratorObject=configuratorClass.getConstructor().newInstance();
				if(configuratorObject instanceof AbstractExecutionConfigurator){
					AbstractExecutionConfigurator configurator=(AbstractExecutionConfigurator)configuratorObject;
					//configurator.setHypervisor(hypervisor);
					configurator.setExecution(machineExecution);
					//TODO Evaluar si hacerlo en el apagado porque es mas importante el tiempo de arranque.
					hypervisor.registerVirtualMachine(this);
	    			hypervisor.restoreVirtualMachineSnapshot(this,"unacloudbase");
	        		hypervisor.configureVirtualMachineHardware(machineExecution.getCores(),machineExecution.getMemory(),this);
	    			hypervisor.startVirtualMachine(this);
	    			configurator.configureHostname();
	    			configurator.configureIP();
	    			System.out.println("image config "+new Date());
	    	        PersistentExecutionManager.startUpMachine(machineExecution,!configurator.doPostConfigure());	    	       
				}else {
					ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), ExecutionStateEnum.FAILED,"Invalid virtual machine configurator.");
					status=ImageStatus.FREE;
				}
				
			} catch (Exception e) {
				e.printStackTrace(System.out);
				ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), ExecutionStateEnum.FAILED,"Configurator class error: "+e.getMessage());
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
		Platform hypervisor=PlatformFactory.getHypervisor(this.getImage().getHypervisorId());
		hypervisor.cloneVirtualMachine(this,dest);
		return dest;
	}
	
	/**
	 * Makes initialization process in copy before starting
	 * @throws VirtualMachineExecutionException
	 */
	public synchronized void init()throws VirtualMachineExecutionException{
		Platform hypervisor=PlatformFactory.getHypervisor(image.getHypervisorId());
		if(hypervisor==null)throw new VirtualMachineExecutionException("Hypervisor doesn't exists on machine. Hypervisor was "+image.getHypervisorId());
		hypervisor.registerVirtualMachine(this);
		try {
			hypervisor.changeVirtualMachineMac(this);
			hypervisor.takeVirtualMachineSnapshot(this,UnaCloudConstants.DEFAULT_IMG_NAME);
		} catch (PlatformOperationException e) {
			e.printStackTrace();
		}
		hypervisor.unregisterVirtualMachine(this);
	}
	
	/**
	 * Delete the snapshot of this image to save it with current state and files
	 * @throws VirtualMachineExecutionException
	 * @throws PlatformOperationException 
	 */
	public synchronized void deleteSnapshot()throws VirtualMachineExecutionException, PlatformOperationException{
		Platform hypervisor=PlatformFactory.getHypervisor(this.getImage().getHypervisorId());
		hypervisor.deleteVirtualMachineSnapshot(this,UnaCloudConstants.DEFAULT_IMG_NAME);		
	}

	
	/**
	 * Starts copy
	 * @throws PlatformOperationException
	 */
	public void startVirtualMachine()throws PlatformOperationException{
		Platform hypervisor=PlatformFactory.getHypervisor(this.getImage().getHypervisorId());
		hypervisor.startVirtualMachine(this);
	}
    
	/**
	 * Executes given command on this copy
	 * @param command command to get executed
	 * @param args command arguments
	 * @throws PlatformOperationException
	 */
    public void executeCommandOnMachine( String command,String...args) throws PlatformOperationException{
    	Platform hypervisor=PlatformFactory.getHypervisor(image.getHypervisorId());
    	hypervisor.executeCommandOnMachine(this,command,args);
    }
    
    /**
     * copies a file in this image clone
     * @param destinationRoute destination path in the VM
     * @param sourceFile original file
     * @throws PlatformOperationException
     */
    public void copyFileOnVirtualMachine(String destinationRoute, File sourceFile) throws PlatformOperationException{
    	Platform hypervisor=PlatformFactory.getHypervisor(image.getHypervisorId());
    	hypervisor.copyFileOnVirtualMachine(this,destinationRoute,sourceFile);
    }
    
    /**
     * Restarts this VM
     * @throws PlatformOperationException
     */
    public void restartVirtualMachine() throws PlatformOperationException{
    	Platform hypervisor=PlatformFactory.getHypervisor(this.getImage().getHypervisorId());
		hypervisor.restartVirtualMachine(this);
    }
    
    /**
     * Finalizes copy execution
     */
    public synchronized void stopAndUnregister(){
    	Platform hypervisor=PlatformFactory.getHypervisor(image.getHypervisorId());
    	hypervisor.stopAndUnregister(this);
    }
    /**
     * Finalizes copy execution without unregistering and freeing image
     */
    public synchronized void stop(){
    	Platform hypervisor=PlatformFactory.getHypervisor(image.getHypervisorId());
    	hypervisor.stopVirtualMachine(this);
    }
    /**
     * Unregistering image copy
     */
    public synchronized void unregister(){
    	Platform hypervisor=PlatformFactory.getHypervisor(image.getHypervisorId());
    	hypervisor.unregisterVirtualMachine(this);
    }
}