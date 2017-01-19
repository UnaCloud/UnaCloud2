package uniandes.unacloud.control.queue;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import uniandes.unacloud.common.com.UnaCloudAbstractMessage;
import uniandes.unacloud.common.com.UnaCloudAbstractResponse;
import uniandes.unacloud.common.com.messages.InvalidOperationResponse;
import uniandes.unacloud.common.com.messages.agent.ClearImageFromCacheMessage;
import uniandes.unacloud.common.com.messages.agent.ClearVMCacheMessage;
import uniandes.unacloud.common.com.messages.agent.StopAgentMessage;
import uniandes.unacloud.common.com.messages.agent.UpdateAgentMessage;
import uniandes.unacloud.common.com.messages.exeo.ExecutionSaveImageMessage;
import uniandes.unacloud.common.com.messages.exeo.ExecutionSaveImageResponse;
import uniandes.unacloud.common.com.messages.exeo.ExecutionStartMessage;
import uniandes.unacloud.common.com.messages.exeo.ExecutionStopMessage;
import uniandes.unacloud.common.com.messages.exeo.ImageNetInterfaceComponent;
import uniandes.unacloud.common.com.messages.exeo.ExecutionStartResponse.ExecutionState;
import uniandes.unacloud.common.enums.ExecutionStateEnum;
import uniandes.unacloud.common.utils.Time;
import uniandes.unacloud.control.communication.processor.AbstractResponseProcessor;
import uniandes.unacloud.control.communication.sender.MessageSender;
import uniandes.unacloud.control.init.ControlManager;
import uniandes.unacloud.share.db.DeploymentManager;
import uniandes.unacloud.share.db.PhysicalMachineManager;
import uniandes.unacloud.share.db.ImageManager;
import uniandes.unacloud.share.entities.DeployedImageEntity;
import uniandes.unacloud.share.entities.DeploymentEntity;
import uniandes.unacloud.share.entities.NetInterfaceEntity;
import uniandes.unacloud.share.entities.PhysicalMachineEntity;
import uniandes.unacloud.share.entities.ExecutionEntity;
import uniandes.unacloud.share.entities.ImageEntity;
import uniandes.unacloud.share.enums.IPEnum;
import uniandes.unacloud.share.enums.PhysicalMachineStateEnum;
import uniandes.unacloud.share.enums.TaskEnum;
import uniandes.unacloud.share.enums.ImageEnum;
import uniandes.unacloud.share.queue.QueueReader;
import uniandes.unacloud.share.queue.messages.MessageAddInstances;
import uniandes.unacloud.share.queue.messages.MessageCreateCopyFromExecution;
import uniandes.unacloud.share.queue.messages.MessageDeployCluster;
import uniandes.unacloud.share.queue.messages.MessageIdOfImage;
import uniandes.unacloud.share.queue.messages.MessageStopExecutions;
import uniandes.unacloud.share.queue.messages.MessageTaskMachines;
import uniandes.unacloud.share.queue.messages.QueueMessage;

/**
 * Class to process each message from queue
 * @author CesarF
 *
 */
public class QueueMessageProcessor implements QueueReader{
	
	/**
	 * Quantity of messages send in each thread
	 */
	private int messagesByThread;
		
	/**
	 * Pool of threads to attend messages
	 */
	private ExecutorService threadPool;
	
	public QueueMessageProcessor(int threads, int messages) {
		threadPool=Executors.newFixedThreadPool(threads);
		this.messagesByThread = messages;
	}

	@Override
	public void processMessage(QueueMessage message) {
		System.out.println("Receive message "+message.getMessage());
		switch (message.getType()) {
		case CLEAR_CACHE:
			clearCache(new MessageIdOfImage(message));
			break;
		case SEND_TASK:	
			sendTaskToAgents(new MessageTaskMachines(message));
			break;
		case DEPLOY_CLUSTER:
			doDeploy(new MessageDeployCluster(message));
			break;
		case STOP_DEPLOYS:	
			stopDeploy(new MessageStopExecutions(message), "Finished by request");
			break;
		case ADD_INSTANCES:	
			addInstances(new MessageAddInstances(message));
			break;
		case CREATE_COPY:
			requestCopy(new MessageCreateCopyFromExecution(message));
			break;
		default:
			break;
		}
	}
	
	/**
	 * Receives image id and process request to remove the image from agents cache
	 * @param message
	 */
	private void clearCache(MessageIdOfImage message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {
			MessageIdOfImage messageId = (MessageIdOfImage) message;
			final Long imageId =  messageId.getIdImage();
			
			ImageEntity image = new ImageEntity(imageId, null, null, ImageEnum.REMOVING_CACHE, null);
			ImageManager.setImage(image, con);
			try {				
				List<PhysicalMachineEntity> machines=PhysicalMachineManager.getAllPhysicalMachine(PhysicalMachineStateEnum.ON, con);	
				if(machines.size()>0){
					for (int i = 0; i < machines.size() ; i+=messagesByThread) {
						threadPool.submit(new MessageSender(machines.subList(i, i+messagesByThread>machines.size()?machines.size():i+messagesByThread), new ClearImageFromCacheMessage(imageId), new AbstractResponseProcessor() {			
							@Override
							public void attendResponse(UnaCloudAbstractResponse response, Long id) {
								try(Connection con2 = ControlManager.getInstance().getDBConnection()){
									ImageEntity image = new ImageEntity(imageId, null, null, ImageEnum.AVAILABLE, null);
									ImageManager.setImage(image, con2);
								}catch (Exception e) {e.printStackTrace();}
							}
							@Override
							public void attendError(String message, Long id) {
								try(Connection con2 = ControlManager.getInstance().getDBConnection()){
									ImageEntity image = new ImageEntity(imageId, null, null, ImageEnum.AVAILABLE, null);
									ImageManager.setImage(image, con2);
									PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
								}catch (Exception e) {e.printStackTrace();}
							}
						}));
					}	
				}else{
					image.setState(ImageEnum.AVAILABLE);
					ImageManager.setImage(image, con);
				}
							
			} catch (Exception e) {
				e.printStackTrace();
				image.setState(ImageEnum.AVAILABLE);
				ImageManager.setImage(image, con);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Sends stop, update o clear cache message to specific list of physical machines
	 * @param messageTask
	 */
	private void sendTaskToAgents(MessageTaskMachines messageTask){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			
			TaskEnum task = messageTask.getTask();
			Long[] ids = messageTask.getIdMachines();
			
			List<PhysicalMachineEntity> machines=PhysicalMachineManager.getPhysicalMachineList(ids,PhysicalMachineStateEnum.PROCESSING, con);
			System.out.println("Sending message to "+machines.size());
			for (int i = 0; i < machines.size() ; i+=messagesByThread) {
				UnaCloudAbstractMessage absMessage = task.equals(TaskEnum.CACHE)?
						new ClearVMCacheMessage():task.equals(TaskEnum.STOP)?
								new StopAgentMessage():new UpdateAgentMessage();
				threadPool.submit(new MessageSender(machines.subList(i, i+messagesByThread>machines.size()?machines.size():i+messagesByThread), 
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
	private void doDeploy(MessageDeployCluster message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {
			Long deploymentId =  message.getIdDeployment();
			
			DeploymentEntity deploy = DeploymentManager.getDeployment(deploymentId, con);
			System.out.println("Deploy "+deploy.getId());
			if(deploy!=null){
				for(DeployedImageEntity image :deploy.getImages()){
					for(final ExecutionEntity execution : image.getExecutions()){
						
						ExecutionStartMessage vmsm = new ExecutionStartMessage();
						System.out.println("Execution from "+execution.getStartTime()+" to: "+execution.getStopTime()+" - "+execution.getTimeInHours()+" - "+execution.getTime());
						vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
						vmsm.setHostname(execution.getHostName());
						vmsm.setVmCores(execution.getCores());
						vmsm.setVmMemory(execution.getRam());
						vmsm.setExecutionId(execution.getId());
						vmsm.setImageId(image.getImage().getId());
						
						List<ImageNetInterfaceComponent> interfaces = new ArrayList<ImageNetInterfaceComponent>();
						for(NetInterfaceEntity interf: execution.getInterfaces())
							interfaces.add(new ImageNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
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
									DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, ExecutionStateEnum.CONFIGURING, null, "Initializing"), con2);
								}catch (Exception e) {e.printStackTrace();}
							}
							@Override
							public void attendError(String message, Long id) {
								try(Connection con2 = ControlManager.getInstance().getDBConnection()){
									PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
									PhysicalMachineManager.setPhysicalMachine(pm, con2);
									DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.FAILED, null, "Communication error"), con2);
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
	 * Sends a message to agents to stop an execution
	 * @param message
	 * @param text to be saved in database in case of success
	 */
	private void stopDeploy(MessageStopExecutions message, final String text){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			Long[] ids = message.getIdExecutions();
			
			List<ExecutionEntity> executions = DeploymentManager.getExecutions(ids,null,false, con);
			for(final ExecutionEntity execution: executions)
				if(execution.getState().equals(ExecutionStateEnum.FINISHED)
						||execution.getState().equals(ExecutionStateEnum.FINISHING)
							||execution.getState().equals(ExecutionStateEnum.FAILED)){
				ExecutionStopMessage vmsm=new ExecutionStopMessage();
				vmsm.setExecutionId(execution.getId());
				List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
				machines.add(execution.getNode());
				threadPool.submit(new MessageSender(machines, 
						vmsm, new AbstractResponseProcessor() {			
					@Override
					public void attendResponse(UnaCloudAbstractResponse response, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, new Date(), null, ExecutionStateEnum.FINISHED, null, text), con2);
							DeploymentManager.breakFreeInterfaces(execution.getId(), con2, IPEnum.AVAILABLE);
						}catch (Exception e) {e.printStackTrace();}
					}
					@Override
					public void attendError(String message, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
							DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.FINISHED, null, "Connection lost to agent, execution will be removed when it reconnects"), con2);
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
	private void addInstances(MessageAddInstances message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			Long imageId = message.getIdImage();
			Long[] ids = message.getIdExecutions();
			
			List<ExecutionEntity> executions = DeploymentManager.getExecutions(ids,ExecutionStateEnum.QUEUED,true, con);
			for(final ExecutionEntity execution : executions) {
				ExecutionStartMessage vmsm = new ExecutionStartMessage();
				vmsm.setExecutionTime(new Time(execution.getTimeInHours(), TimeUnit.HOURS));
				vmsm.setHostname(execution.getHostName());
				vmsm.setVmCores(execution.getCores());
				vmsm.setVmMemory(execution.getRam());
				vmsm.setExecutionId(execution.getId());
				vmsm.setImageId(imageId);
				List<ImageNetInterfaceComponent> interfaces = new ArrayList<ImageNetInterfaceComponent>();
				for(NetInterfaceEntity interf: execution.getInterfaces())
					interfaces.add(new ImageNetInterfaceComponent(interf.getIp(), interf.getNetMask(),interf.getName()));
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
							DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, new Date(), stopTime, null, ExecutionStateEnum.CONFIGURING, null,"Initializing"), con2);
						}catch (Exception e) {e.printStackTrace();}
					}
					@Override
					public void attendError(String message, Long id) {
						try(Connection con2 = ControlManager.getInstance().getDBConnection()){
							PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
							PhysicalMachineManager.setPhysicalMachine(pm, con2);
							DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.FAILED, null,"Communication error"), con2);
						}catch (Exception e) {e.printStackTrace();}
					}
				}));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to one agent to request send a current execution to server
	 * @param message
	 */
	private void requestCopy(MessageCreateCopyFromExecution message){
		try(Connection con = ControlManager.getInstance().getDBConnection();) {	
			Long executionId = message.getIdExecution();
			Long newImageId = message.getIdImage();
			Long oldImageId = message.getIdPastImage();
			
			final ExecutionEntity execution = DeploymentManager.getExecution(executionId, ExecutionStateEnum.REQUEST_COPY, con);
			if(execution!=null){
				final ImageEntity image = ImageManager.getImage(newImageId, ImageEnum.COPYING, con);
				if(image!=null){
					ExecutionSaveImageMessage vmsim = new ExecutionSaveImageMessage();
					vmsim.setTokenCom(image.getToken());
					vmsim.setImageId(oldImageId);
					vmsim.setExecutionId(execution.getId());
					List<PhysicalMachineEntity> machines = new ArrayList<PhysicalMachineEntity>();
					machines.add(execution.getNode());
					threadPool.submit(new MessageSender(machines, 
							vmsim, new AbstractResponseProcessor() {			
						@Override
						public void attendResponse(UnaCloudAbstractResponse response, Long id) {
							try(Connection con2 = ControlManager.getInstance().getDBConnection()){
								if(response instanceof ExecutionSaveImageResponse){
									if(((ExecutionSaveImageResponse)response).getState().equals(ExecutionState.COPYNG)){
										DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.COPYING, null, null), con2);
									}else{
										DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.DEPLOYED, null, ((ExecutionSaveImageResponse)response).getMessage()), con2);
										ImageManager.deleteImage(image, con2);
									}
								}else{
									DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.DEPLOYED, null, ((InvalidOperationResponse)response).getMessage()), con2);
									ImageManager.deleteImage(image, con2);
								}
							}catch (Exception e) {e.printStackTrace();}
						}
						@Override
						public void attendError(String message, Long id) {
							try(Connection con2 = ControlManager.getInstance().getDBConnection()){
								PhysicalMachineEntity pm = new PhysicalMachineEntity(id, null, null, PhysicalMachineStateEnum.OFF);
								PhysicalMachineManager.setPhysicalMachine(pm,con2);
								DeploymentManager.setExecution(new ExecutionEntity(execution.getId(), 0, 0, null, null, null, ExecutionStateEnum.DEPLOYED, null, "Error copying image"), con2);
								ImageManager.deleteImage(image, con2);
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
