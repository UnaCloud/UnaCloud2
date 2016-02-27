package uniandes.communication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudAbstractResponse;
import unacloud.share.entities.PhysicalMachineEntity;
import uniandes.ControlManager;
import uniandes.communication.processor.AbstractResponseProcessor;

/**
 * Classes to send message to list of machines
 * @author Cesar
 *
 */
public class MessageSender extends Thread{
	
	private List<PhysicalMachineEntity> machines;
	private UnaCloudAbstractMessage message;
	private AbstractResponseProcessor processor;
	
	
	public MessageSender(List<PhysicalMachineEntity> machines,UnaCloudAbstractMessage message, AbstractResponseProcessor processor) {
		this.machines = machines;
		this.message = message;
		this.processor = processor;
	}
	
	@Override
	public void run() {
		for(PhysicalMachineEntity pm: machines){
			try{
				System.out.println("Sending message to "+pm.getIp()+":"+ControlManager.getInstance().getPort());
				Socket s=new Socket(pm.getIp(),ControlManager.getInstance().getPort());
				//s.setSoTimeout(15000);
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				oos.writeObject(message);
				oos.flush();
				try {
					processor.attendResponse((UnaCloudAbstractResponse) ois.readObject(),pm.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}				
				s.close();
			}catch(Exception e){
				e.printStackTrace();
				System.out.println("Error connectiong to "+pm.getIp());		
				processor.attendError(e.getMessage(),pm.getId());
			}
			try{
				Thread.sleep(500);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

}
