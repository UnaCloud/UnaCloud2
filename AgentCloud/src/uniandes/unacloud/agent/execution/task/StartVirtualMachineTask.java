package uniandes.unacloud.agent.execution.task;

import uniandes.unacloud.agent.communication.send.ServerMessageSender;
import uniandes.unacloud.agent.exceptions.VirtualMachineExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.entities.ImageCopy;
import uniandes.unacloud.agent.execution.entities.Execution;
import uniandes.unacloud.common.enums.ExecutionStateEnum;

/**
 * Task to start a virtual machine execution
 * @author CesarF
 *
 */
public class StartVirtualMachineTask implements Runnable{
	Execution machineExecution;
	/**
	 * class constructor
	 * @param machineExecution VM instance to be started
	 */
	public StartVirtualMachineTask(Execution machineExecution) {
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
				ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), ExecutionStateEnum.FAILED,ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
