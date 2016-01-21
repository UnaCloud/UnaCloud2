package uniandes.communication;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import communication.UnaCloudAbstractMessage;
import communication.UnaCloudAbstractResponse;
import unacloud.entities.PhysicalMachine;
import uniandes.ControlManager;

/**
 * Classes to send message to a list of machines
 * @author Cesar
 *
 */
public class MessageSender extends Thread{
	
	private List<PhysicalMachine> machines;
	private UnaCloudAbstractMessage message;
	private ResponseProcessor processor;
	
	
	public MessageSender(List<PhysicalMachine> machines,UnaCloudAbstractMessage message, ResponseProcessor processor) {
		this.machines = machines;
		this.message = message;
		this.processor = processor;
	}
	
	@Override
	public void run() {
		for(PhysicalMachine pm: machines){
			try{
				Socket s=new Socket(pm.getIp(),ControlManager.getInstance().getPort());
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
