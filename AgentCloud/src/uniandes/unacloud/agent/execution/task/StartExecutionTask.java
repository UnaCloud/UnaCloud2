package uniandes.unacloud.agent.execution.task;

import uniandes.unacloud.agent.exceptions.ExecutionException;
import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;

/**
 * Task to start an execution
 * @author CesarF
 *
 */
public class StartExecutionTask implements Runnable {
	
	/**
	 * Machine execution
	 */
	private Execution machineExecution;
	
	/**
	 * class constructor
	 * @param machineExecution Execution instance to be started
	 */
	public StartExecutionTask(Execution machineExecution) {
		this.machineExecution = machineExecution;
	}
	
	/**
	 * Executes start machine task
	 */
	@Override
	public void run() {
		System.out.println("Start Execution");
		try {
			//get image 
			ImageCopy image = ImageCacheManager.getFreeImageCopy(machineExecution.getImageId());
			System.out.println("Get Image");
			machineExecution.setImage(image);
			image.configureAndStart(machineExecution);
			System.out.println("endStartExecution");
		} catch (ExecutionException ex) {
			try {
				ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.FAIL, ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
