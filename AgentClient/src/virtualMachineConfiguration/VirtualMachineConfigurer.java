package virtualMachineConfiguration;

import Exceptions.VirtualMachineExecutionException;
import hypervisorManager.ImageCopy;
import communication.ServerMessageSender;
import communication.messages.vmo.VirtualMachineStartResponse;
import unacloudEnums.VirtualMachineExecutionStateEnum;
import virtualMachineManager.VirtualMachineExecution;
import virtualMachineManager.ImageCacheManager;

public final class VirtualMachineConfigurer extends Thread{
	/**
	 * VM to be configured
	 */
	VirtualMachineExecution machineExecution;
	
	/**
	 * Class constructor
	 * @param machineExecution VM
	 */
	public VirtualMachineConfigurer(VirtualMachineExecution machineExecution) {
		this.machineExecution = machineExecution;
	}
	
	/**
	 * Starts a new thread with configuration
	 * @return response of start process
	 */
	public VirtualMachineStartResponse startProcess(){
		VirtualMachineStartResponse resp=new VirtualMachineStartResponse();
		resp.setState(VirtualMachineStartResponse.VirtualMachineState.STARTING);
		resp.setMessage("Starting virtual machine...");
		start();
		return resp;
	}
	/**
	 * Thread run modification. Starts configuration process
	 */
	@Override
	public void run() {
		System.out.println("startVirtualMachine");
		try{
			ImageCopy image=ImageCacheManager.getFreeImageCopy(machineExecution.getImageId());
			machineExecution.setImage(image);
			image.configureAndStart(machineExecution);
		}catch(VirtualMachineExecutionException ex){
			ServerMessageSender.reportVirtualMachineState(machineExecution.getId(), VirtualMachineExecutionStateEnum.FAILED,ex.getMessage());
		}
		
	}
}
