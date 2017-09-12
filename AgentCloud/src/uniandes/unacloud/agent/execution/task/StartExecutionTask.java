package uniandes.unacloud.agent.execution.task;

import uniandes.unacloud.agent.execution.ImageCacheManager;
import uniandes.unacloud.agent.execution.domain.Execution;
import uniandes.unacloud.agent.execution.domain.ImageCopy;
import uniandes.unacloud.agent.net.send.ServerMessageSender;
import uniandes.unacloud.common.enums.ExecutionProcessEnum;
import uniandes.unacloud.common.enums.TransmissionProtocolEnum;

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
	 * Transmission type to request image
	 */
	private TransmissionProtocolEnum transmissionType;
	
	/**
	 * class constructor
	 * @param machineExecution Execution instance to be started
	 */
	public StartExecutionTask(Execution machineExecution, TransmissionProtocolEnum trans) {
		this.machineExecution = machineExecution;
		this.transmissionType = trans;
	}
	
	/**
	 * Executes start machine task
	 */
	@Override
	public void run() {
		System.out.println("Start Execution " + machineExecution.getId());
		try {
			//get image 
			ImageCopy image = ImageCacheManager.getFreeImageCopy(machineExecution, transmissionType);
			machineExecution.setImage(image);
			image.configureAndStart(machineExecution);
			System.out.println("endStartExecution");
		} catch (Exception ex) {
			try {
				ServerMessageSender.reportExecutionState(machineExecution.getId(), ExecutionProcessEnum.FAIL, ex.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
}
