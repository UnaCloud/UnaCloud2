package uniandes.queue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.losandes.utils.Constants;
import com.losandes.utils.Time;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudAbstractResponse;
import communication.messages.ao.ClearImageFromCacheMessage;
import communication.messages.ao.ClearVMCacheMessage;
import communication.messages.ao.StopAgentMessage;
import communication.messages.ao.UpdateAgentMessage;
import communication.messages.vmo.VirtualMachineStartMessage;
import communication.messages.vmo.VirtualNetInterfaceComponent;
import db.DeploymentManager;
import db.PhysicalMachineManager;
import db.VirtualImageManager;
import queue.QueueMessage;
import queue.QueueReader;
import unacloud.entities.DeployedImage;
import unacloud.entities.Deployment;
import unacloud.entities.NetInterface;
import unacloud.entities.PhysicalMachine;
import unacloud.entities.VirtualMachineExecution;
import unacloud.entities.VirtualMachineImage;
import unacloud.enums.PhysicalMachineStateEnum;
import unacloud.enums.TaskEnum;
import unacloud.enums.VirtualMachineExecutionStateEnum;
import unacloud.enums.VirtualMachineImageEnum;
import uniandes.communication.MessageSender;
import uniandes.communication.ResponseProcessor;

/**
 * Class to process each message from queue
 * @author Cesar
 *
 */
public class QueueMessageProcessor implements QueueReader{
	
	private ExecutorService threadPool=Executors.newFixedThreadPool(3);

	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receive message "+message.getType());
		switch (message.getType()) {
		case CLEAR_CACHE:	
			clearCache(message);
			break;
		case SEND_TASK:			
			sendTaskToAgents(message);
			break;
		case DEPLOY_CLUSTER:
			doDeploy(message);
			break;
		case STOP_DEPLOYS:	
			System.out.println("Stop deploy");
			break;
		case ADD_INSTANCES:		
			System.out.println("Add Instances");
			break;
		case CREATE_COPY:	
			System.out.println("Create copy");
			break;
		default:
			break;
		}
	}
	
	/**
	 * Get virtual image in queuemessage and process request to remove the image from agents cache
	 * @param message
	 */
	private void clearCache(QueueMessage message){
		try {		
			final Long imageId =  Long.parseLong(message.getMessageParts()[0]);
			VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.REMOVING_CACHE);
			VirtualImageManager.setVirtualMachine(image);
			try {				
				List<PhysicalMachine> machines=PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON);			
				for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
					threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), new ClearImageFromCacheMessage(imageId), new ResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.AVAILABLE);
							VirtualImageManager.setVirtualMachine(image);
						}
						@Override
						public void attendError(String message, Long id) {
							VirtualMachineImage image = new VirtualMachineImage(imageId, null, null, VirtualMachineImageEnum.AVAILABLE);
							VirtualImageManager.setVirtualMachine(image);
							PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm);
						}
					}));
				}				
			} catch (Exception e) {
				image.setState(VirtualMachineImageEnum.AVAILABLE);
				VirtualImageManager.setVirtualMachine(image);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Method to send stop, update o clear cache message to specific list of physical machines
	 * @param message
	 */
	private void sendTaskToAgents(QueueMessage message){
		try {
			TaskEnum task = TaskEnum.getEnum(message.getMessageParts()[0]);
			Long[] ids = new Long[message.getMessageParts().length-1];
			for (int i = 1, j=0; i < message.getMessageParts().length; i++, j++) {
				ids[j]=Long.parseLong(message.getMessageParts()[i]);
			}
			List<PhysicalMachine> machines=PhysicalMachineManager.getPhysicalMachineList(ids,PhysicalMachineStateEnum.PROCESSING);			
			for (int i = 0; i < machines.size(); i+=Constants.AGENT_QUANTITY_MESSAGE) {
				UnaCloudAbstractMessage absMessage = task.equals(TaskEnum.CACHE)?
						new ClearVMCacheMessage():task.equals(TaskEnum.STOP)?
								new StopAgentMessage():new UpdateAgentMessage();
				threadPool.submit(new MessageSender(machines.subList(i, i+Constants.AGENT_QUANTITY_MESSAGE), 
						absMessage, new ResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.ON);
						PhysicalMachineManager.setPhysicalMachine(pm);
					}
					@Override
					public void attendError(String message, Long id) {
						PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
						PhysicalMachineManager.setPhysicalMachine(pm);
					}
				}));
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to send message to agents to start deploy in physical machines
	 * @param message
	 */
	private void doDeploy(QueueMessage message){
		try {
			Long deploymentId =  Long.parseLong(message.getMessageParts()[0]);
			Deployment deploy = DeploymentManager.getDeployment(deploymentId);
			if(deploy!=null){
				for(DeployedImage image :deploy.getImages()){
					for(final VirtualMachineExecution execution : image.getExecutions()){
						VirtualMachineStartMessage vmsm = new VirtualMachineStartMessage();
						vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
						vmsm.setHostname(execution.getHostName());
						vmsm.setVmCores(execution.getCores());
						vmsm.setVmMemory(execution.getRam());
						vmsm.setVirtualMachineExecutionId(execution.getId());
						vmsm.setVirtualMachineImageId(image.getImage().getId());
						List<VirtualNetInterfaceComponent> interfaces = new ArrayList<VirtualNetInterfaceComponent>();
						for(NetInterface interf: execution.getInterfaces())
							interfaces.add(new VirtualNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
						vmsm.setInterfaces(interfaces);						
						List<PhysicalMachine> machines = new ArrayList<PhysicalMachine>();
						machines.add(execution.getNode());
						threadPool.submit(new MessageSender(machines, 
								vmsm, new ResponseProcessor() {			
							@Override
							public void attendResponse(UnaCloudAbstractResponse response, Long id) {
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.CONFIGURING, null));
								//TODO realizar el cambio de hora
							}
							@Override
							public void attendError(String message, Long id) {
								PhysicalMachine pm = new PhysicalMachine(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm);
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecution(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FAILED, null));
								//TODO set stop and start time
							}
						}));
					}
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
