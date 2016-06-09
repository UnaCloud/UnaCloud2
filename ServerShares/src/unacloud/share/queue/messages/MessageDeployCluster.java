package unacloud.share.queue.messages;

import org.json.JSONObject;

import unacloud.share.enums.QueueMessageType;
import unacloud.share.queue.QueueMessage;

/**
 * Class to represent a Message about Deploy Cluster
 * @author cdsbarrera
 *
 */
public class MessageDeployCluster extends QueueMessage{
	
	private static final String TAG_DEPLOYMENT = "id_deployment";
	
	public MessageDeployCluster(String requester, long idDeployment){
		super(requester);
		this.setType(QueueMessageType.DEPLOY_CLUSTER);
		
		JSONObject temp = this.getMessageContent();
		temp.put(TAG_DEPLOYMENT, idDeployment);
		this.setMessageContent(temp);
	}
	
	/**
	 * Return the ID of Deployment
	 * @return
	 */
	public long getIdDeployment() {
		JSONObject temp = this.getMessageContent();
		return temp.getLong(TAG_DEPLOYMENT);
	}
}
