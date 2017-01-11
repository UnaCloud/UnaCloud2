package uniandes.unacloud.control.communication.processor;

import java.sql.SQLException;

import org.json.JSONException;

import uniandes.unacloud.common.com.messages.udp.UnaCloudMessageUDP;

/**
 * Class to process UDP messages sent by agents
 * @author CesarF
 *
 */
public abstract class AbstractReceiverProcessor extends Thread{
	
	private UnaCloudMessageUDP message;

	public AbstractReceiverProcessor(UnaCloudMessageUDP message) {
		this.message = message;
	}
	
	@Override
	public void run() {		
		try {
			processMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * To be implemented to process message
	 * @param message
	 * @throws JSONException
	 */
	public abstract void processMessage(UnaCloudMessageUDP message)  throws JSONException , SQLException;
	

}
