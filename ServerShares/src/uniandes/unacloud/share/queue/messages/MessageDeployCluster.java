package uniandes.unacloud.share.queue.messages;

import org.json.JSONObject;

import uniandes.unacloud.common.enums.TransmissionProtocolEnum;
import uniandes.unacloud.share.enums.QueueMessageType;

/**
 * Class to represent a Message about Deploy Cluster
 * @author cdsbarrera
 * @author CesarF
 *
 */
public class MessageDeployCluster extends QueueMessage {
	
	private static final String TAG_DEPLOYMENT = "id_deployment";
	
	private static final String TAG_TRANSMISSION = "transmission_type";
	
	public MessageDeployCluster(String requester, long idDeployment, TransmissionProtocolEnum transType) {
		super(requester);
		this.setType(QueueMessageType.DEPLOY_CLUSTER);
		
		JSONObject temp = this.getMessageContent();
		temp.put(TAG_DEPLOYMENT, idDeployment);
		temp.put(TAG_TRANSMISSION, transType);
		this.setMessageContent(temp);
	}
	
	public MessageDeployCluster(QueueMessage message) {
		super(message.getRequester());
		this.setType(message.getType());
		this.setMessageContent(message.getMessageContent());
	}
	
	public TransmissionProtocolEnum getTypeTransmission() {
		JSONObject temp = this.getMessageContent();
		try {
			return TransmissionProtocolEnum.getEnum(temp.getString(TAG_TRANSMISSION));
		} catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Return the ID of Deployment
	 * @return deployment id
	 */
	public Long getIdDeployment() {
		JSONObject temp = this.getMessageContent();
		try {
			return temp.getLong(TAG_DEPLOYMENT);
		} catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
}
