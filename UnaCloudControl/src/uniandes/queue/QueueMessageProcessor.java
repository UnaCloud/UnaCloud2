package uniandes.queue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.losandes.enums.VirtualMachineExecutionStateEnum;
import com.losandes.utils.Time;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudAbstractResponse;
import communication.messages.InvalidOperationResponse;
import communication.messages.ao.ClearImageFromCacheMessage;
import communication.messages.ao.ClearVMCacheMessage;
import communication.messages.ao.StopAgentMessage;
import communication.messages.ao.UpdateAgentMessage;
import communication.messages.vmo.VirtualMachineSaveImageMessage;
import communication.messages.vmo.VirtualMachineSaveImageResponse;
import communication.messages.vmo.VirtualMachineStartMessage;
import communication.messages.vmo.VirtualMachineStartResponse.VirtualMachineState;
import communication.messages.vmo.VirtualMachineStopMessage;
import communication.messages.vmo.VirtualNetInterfaceComponent;
import unacloud.share.db.DeploymentManager;
import unacloud.share.db.PhysicalMachineManager;
import unacloud.share.db.VirtualImageManager;
import unacloud.share.queue.QueueMessage;
import unacloud.share.queue.QueueReader;
import unacloud.share.queue.messages.MessageAddInstances;
import unacloud.share.queue.messages.MessageCreateCopyFromExecution;
import unacloud.share.queue.messages.MessageDeployCluster;
import unacloud.share.queue.messages.MessageIdOfImage;
import unacloud.share.queue.messages.MessageStopExecutions;
import unacloud.share.queue.messages.MessageTaskMachines;
import unacloud.share.entities.DeployedImageEntity;
import unacloud.share.entities.DeploymentEntity;
import unacloud.share.entities.NetInterfaceEntity;
import unacloud.share.entities.PhysicalMachineEntity;
import unacloud.share.entities.VirtualMachineExecutionEntity;
import unacloud.share.entities.VirtualMachineImageEntity;
import unacloud.share.enums.IPEnum;
import unacloud.share.enums.PhysicalMachineStateEnum;
import unacloud.share.enums.TaskEnum;
import unacloud.share.enums.VirtualMachineImageEnum;
import uniandes.ControlManager;
import uniandes.communication.MessageSender;
import uniandes.communication.processor.AbstractResponseProcessor;

/**
 * Class to process each message from queue
 * @author CesarF
 *
 */
public class QueueMessageProcessor implements QueueReader{
	
	private int threads;
	
	/**
	 * Pool of threads to attend messages
	 */
	private ExecutorService threadPool;
	
	public QueueMessageProcessor(int threads) {
		threadPool=Executors.newFixedThreadPool(threads);
		this.threads=threads;
	}

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
			stopDeploy(message, "Finished by request");
			break;
		case ADD_INSTANCES:	
			addInstances(message);
			break;
		case CREATE_COPY:
			requestCopy(message);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Get virtual image in queue message and process request to remove the image from agents cache
	 * @param message
	 */
	private void clearCache(QueueMessage message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {
			MessageIdOfImage messageId = (MessageIdOfImage) message;
			final Long imageId =  messageId.getIdImage();
			
			VirtualMachineImageEntity image = new VirtualMachineImageEntity(imageId, null, null, VirtualMachineImageEnum.REMOVING_CACHE, null);
			VirtualImageManager.setVirtualMachine(image, con);
			try {				
				List<PhysicalMachineEntity> machines=PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON, con);	
				if(machines.size()>0){
					for (int i = 0; i < machines.size(); i+=threads+1) {
						threadPool.submit(new MessageSender(machines.subList(i, i+threads>machines.size()?machines.size():i+threads), new ClearImageFromCacheMessage(imageId), new AbstractResponseProcessor() {			
							@Override
							public void attendResponse(UnaCloudAbstractResponse response, Long id) {
								try(Connection con2 = ControlManager.getInstance().getDBConnection()){
									VirtualMachineImageEntity image = new VirtualMachineImageEntity(imageId, null, null, VirtualMachineImageEnum.AVAILABLE, null);
									VirtualImageManager.setVirtualMachine(image, con2);
								}catch (Exception e) {e.printStackTrace();}
							}
							@Override
							public void attendError(String message, Long id) {
								try(Connection con2 = ControlManager.getInstance().getDBConnection()){
									VirtualMachineImageEntity image = new VirtualMachineImageEntity(imageId, null, null, VirtualMachineImageEnum.AVAILABLE, null);
									VirtualImageManager.setVirtualMachine(image, con2);
									PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
								}catch (Exception e) {e.printStackTrace();}
							}
						}));
					}	
				}else{
					image.setState(VirtualMachineImageEnum.AVAILABLE);
					VirtualImageManager.setVirtualMachine(image, con);
				}
							
			} catch (Exception e) {
				e.printStackTrace();
				image.setState(VirtualMachineImageEnum.AVAILABLE);
				VirtualImageManager.setVirtualMachine(image, con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Sends stop, update o clear cache message to specific list of physical machines
	 * @param message
	 */
	private void sendTaskToAgents(QueueMessage message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			MessageTaskMachines messageTask = (MessageTaskMachines) message;
			TaskEnum task = messageTask.getTask();
			Long[] ids = messageTask.getIdMachines();
			
			List<PhysicalMachineEntity> machines=PhysicalMachineManager.getPhysicalMachineList(ids,PhysicalMachineStateEnum.PROCESSING, con);			
			for (int i = 0; i < machines.size(); i+=threads+1) {
				UnaCloudAbstractMessage absMessage = task.equals(TaskEnum.CACHE)?
						new ClearVMCacheMessage():task.equals(TaskEnum.STOP)?
								new StopAgentMessage():new UpdateAgentMessage();
				threadPool.submit(new MessageSender(machines.subList(i, i+threads>machines.size()?machines.size():i+threads), 
						absMessage, new AbstractResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.ON);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
						}catch (Exception e) {e.printStackTrace();}
					}
					@Override
					public void attendError(String message, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
						}catch (Exception e) {e.printStackTrace();}
					}
				}));
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends message to agents to start deploy in physical machines
	 * @param message
	 */
	private void doDeploy(QueueMessage message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {
			MessageDeployCluster messageDeploy = (MessageDeployCluster) message;
			Long deploymentId =  messageDeploy.getIdDeployment();
			
			DeploymentEntity deploy = DeploymentManager.getDeployment(deploymentId, con);
			System.out.println("Deploy "+deploy.getId());
			if(deploy!=null){
				for(DeployedImageEntity image :deploy.getImages()){
					for(final VirtualMachineExecutionEntity execution : image.getExecutions()){
						VirtualMachineStartMessage vmsm = new VirtualMachineStartMessage();
						System.out.println("Execution from "+execution.getStartTime()+" to: "+execution.getStopTime()+" - "+execution.getTimeInHours()+" - "+execution.getTime());
						vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
						vmsm.setHostname(execution.getHostName());
						vmsm.setVmCores(execution.getCores());
						vmsm.setVmMemory(execution.getRam());
						vmsm.setVirtualMachineExecutionId(execution.getId());
						vmsm.setVirtualMachineImageId(image.getImage().getId());
						List<VirtualNetInterfaceComponent> interfaces = new ArrayList<VirtualNetInterfaceComponent>();
						for(NetInterfaceEntity interf: execution.getInterfaces())
							interfaces.add(new VirtualNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
						vmsm.setInterfaces(interfaces);						
						List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
						machines.add(execution.getNode());
						threadPool.submit(new MessageSender(machines, 
								vmsm, new AbstractResponseProcessor() {			
							@Override
							public void attendResponse(UnaCloudAbstractResponse response, Long id) {
								try(Connection con2 = ControlManager.getInstance().getDBConnection()){
									Date stopTime = new Date();
									stopTime.setTime(stopTime.getTime()+execution.getTime());
									DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, VirtualMachineExecutionStateEnum.CONFIGURING, null, "Initializing"), con2);
								}catch (Exception e) {e.printStackTrace();}
							}
							@Override
							public void attendError(String message, Long id) {
								try(Connection con2 = ControlManager.getInstance().getDBConnection()){
									PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
									DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.FAILED, null, "Communication error"), con2);
								}catch (Exception e) {e.printStackTrace();}
							}
						}));
					}
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to be used by other classes to stop deployments without use queue
	 * TODO: be careful user is not sent in message
	 * @param executionIds
	 */
	public void remoteStopDeploy(Long[] executionIds){
		for (int i = 0; i < executionIds.length; i++) {
			System.out.println("\t Stop: "+executionIds[i]);
		}
		MessageStopExecutions message = new MessageStopExecutions("0", executionIds);
		stopDeploy(message,"Execution is not running in server");
	}
	
	/**
	 * Sends a message to agents to stop a virtual machine execution
	 * @param message
	 * @param text to be saved in database in case of success
	 */
	private void stopDeploy(QueueMessage message, final String text){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			MessageStopExecutions messageStop = (MessageStopExecutions) message;
			Long[] ids = messageStop.getIdExecutions();
			
			List<VirtualMachineExecutionEntity> executions = DeploymentManager.getExecutions(ids,null,false, con);
			for(final VirtualMachineExecutionEntity execution: executions)
				if(execution.getState().equals(VirtualMachineExecutionStateEnum.FINISHED)
						||execution.getState().equals(VirtualMachineExecutionStateEnum.FINISHING)
							||execution.getState().equals(VirtualMachineExecutionStateEnum.FAILED)){
				VirtualMachineStopMessage vmsm=new VirtualMachineStopMessage();
				vmsm.setVirtualMachineExecutionId(execution.getId());
				List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new AbstractResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, VirtualMachineExecutionStateEnum.FINISHED, null, text), con2);
							DeploymentManager.breakFreeInterfaces(execution.getId(), con2, IPEnum.AVAILABLE);
						}catch (Exception e) {e.printStackTrace();}
					}
					@Override
					public void attendError(String message, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
							DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.FINISHED, null, "Connection lost to agent, execution will be removed when it reconnects"), con2);
							DeploymentManager.breakFreeInterfaces(execution.getId(), con2, IPEnum.AVAILABLE);
						}catch (Exception e) {e.printStackTrace();}
					}
				}));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends message to agents to add physical machines 
	 * @param message
	 */
	private void addInstances(QueueMessage message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			MessageAddInstances messageInstances = (MessageAddInstances) message;
			Long imageId = messageInstances.getIdImage();
			Long[] ids = messageInstances.getIdExecutions();
			
			List<VirtualMachineExecutionEntity> executions = DeploymentManager.getExecutions(ids,VirtualMachineExecutionStateEnum.QUEUED,true, con);
			for(final VirtualMachineExecutionEntity execution : executions) {
				VirtualMachineStartMessage vmsm = new VirtualMachineStartMessage();
				vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
				vmsm.setHostname(execution.getHostName());
				vmsm.setVmCores(execution.getCores());
				vmsm.setVmMemory(execution.getRam());
				vmsm.setVirtualMachineExecutionId(execution.getId());
				vmsm.setVirtualMachineImageId(imageId);
				List<VirtualNetInterfaceComponent> interfaces = new ArrayList<VirtualNetInterfaceComponent>();
				for(NetInterfaceEntity interf: execution.getInterfaces())
					interfaces.add(new VirtualNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
				vmsm.setInterfaces(interfaces);						
				List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new AbstractResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							Date stopTime = new Date();
							stopTime.setTime(stopTime.getTime()+execution.getTime());
							DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, VirtualMachineExecutionStateEnum.CONFIGURING, null,"Initializing"), con2);
						}catch (Exception e) {e.printStackTrace();}
					}
					@Override
					public void attendError(String message, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
							DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.FAILED, null,"Communication error"), con2);
						}catch (Exception e) {e.printStackTrace();}
					}
				}));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to one agent to request send a current virtual execution to server
	 * @param message
	 */
	private void requestCopy(QueueMessage message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			MessageCreateCopyFromExecution messageCreate = (MessageCreateCopyFromExecution) message;
			Long executionId = messageCreate.getIdExecution();
			Long newImageId = messageCreate.getIdImage();
			Long oldImageId = messageCreate.getIdPastImage();
			
			final VirtualMachineExecutionEntity execution = DeploymentManager.getExecution(executionId, VirtualMachineExecutionStateEnum.REQUEST_COPY, con);
			if(execution!=null){
				final VirtualMachineImageEntity image = VirtualImageManager.getVirtualMachine(newImageId, VirtualMachineImageEnum.COPYING, con);
				if(image!=null){
					VirtualMachineSaveImageMessage vmsim = new VirtualMachineSaveImageMessage();
					vmsim.setTokenCom(image.getToken());
					vmsim.setImageId(oldImageId);
					vmsim.setVirtualMachineExecutionId(execution.getId());
					List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
					machines.add(execution.getNode());
					threadPool.submit(new MessageSender(machines, 
							vmsim, new AbstractResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							try(Connection con2 = ControlManager.getInstance().getDBConnection()){
								if(response instanceof VirtualMachineSaveImageResponse){
									if(((VirtualMachineSaveImageResponse)response).getState().equals(VirtualMachineState.COPYNG)){
										DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.COPYING, null, null), con2);
									}else{
										DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.DEPLOYED, null, ((VirtualMachineSaveImageResponse)response).getMessage()), con2);
										VirtualImageManager.deleteVirtualMachineImage(image, con2);
									}
								}else{
									DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.DEPLOYED, null, ((InvalidOperationResponse)response).getMessage()), con2);
									VirtualImageManager.deleteVirtualMachineImage(image, con2);
								}
							}catch (Exception e) {e.printStackTrace();}
						}
						@Override
						public void attendError(String message, Long id) {
							try(Connection con2 = ControlManager.getInstance().getDBConnection()){
								PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm,con2);
								DeploymentManager.setVirtualMachineExecution(new VirtualMachineExecutionEntity(execution.getId(), 0, 0, null, null, null, VirtualMachineExecutionStateEnum.DEPLOYED, null, "Error copying image"), con2);
								VirtualImageManager.deleteVirtualMachineImage(image, con2);
							}catch (Exception e) {e.printStackTrace();}
						}
					}));
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
