package uniandes.unacloud.share.queue.messages;

import org.json.JSONObject;

import uniandes.unacloud.share.enums.QueueMessageType;

/**
 * Class to represent a Message about Deploy Cluster
 * @author cdsbarrera
 *
 */
public class MessageDeployCluster extends QueueMessage{
	
	private static final String TAG_DEPLOYMENT = "id_deployment";
	private static final String TAG_TYPE = "deployment_type";
	
	public MessageDeployCluster(String requester, long idDeployment, int tipo){
		super(requester);
		this.setType(QueueMessageType.DEPLOY_CLUSTER);
		
		JSONObject temp = this.getMessageContent();
		temp.put(TAG_DEPLOYMENT, idDeployment);
		temp.put(TAG_TYPE, tipo);
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
	
	public Integer getDeploymentType() {
		JSONObject temp = this.getMessageContent();
		try {
			return temp.getInt(TAG_TYPE);
		} catch(Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
}
