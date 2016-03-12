package tasks;

import com.losandes.enums.VirtualMachineExecutionStateEnum;

import reportManager.ServerMessageSender;
import exceptions.VirtualMachineExecutionException;
import hypervisorManager.ImageCopy;
import virtualMachineManager.ImageCacheManager;
import virtualMachineManager.entities.VirtualMachineExecution;

public class StartVirtualMachineTask implements Runnable{
	VirtualMachineExecution machineExecution;
	/**
	 * class constructor
	 * @param machineExecution VM instance to be started
	 */
	public StartVirtualMachineTask(VirtualMachineExecution machineExecution) {
		this.machineExecution = machineExecution;
	}
	
	/**
	 * Executes start machine task
	 */
	@Override
	public void run() {
		System.out.println("StartVirtualMachine");
		try{
			//get image 
			ImageCopy image=ImageCacheManager.getFreeImageCopy(machineExecution.getImageId());
			System.out.println("Get Image");
			machineExecution.setImage(image);
			image.configureAndStart(machineExecution);
			System.out.println("endStartVirtualMachine");
		}catch(VirtualMachineExecutionException ex){
			try {
				ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), VirtualMachineExecutionStateEnum.FAILED,ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
