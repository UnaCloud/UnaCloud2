package uniandes.unacloud.share.queue.messages;

import org.json.JSONObject;

import uniandes.unacloud.share.enums.QueueMessageType;

/**
 * Class to represent a Message about Deploy Cluster
 * @author cdsbarrera
 *
 */
public class MessageDeployCluster extends QueueMessage {
	
	private static final String TAG_DEPLOYMENT = "id_deployment";
	
	public MessageDeployCluster(String requester, long idDeployment) {
		super(requester);
		this.setType(QueueMessageType.DEPLOY_CLUSTER);
		
		JSONObject temp = this.getMessageContent();
		temp.put(TAG_DEPLOYMENT, idDeployment);
		this.setMessageContent(temp);
	}
	
	public MessageDeployCluster(QueueMessage message) {
		super(message.getRequester());
		this.setType(message.getType());
		this.setMessageContent(message.getMessageContent());
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
