package uniandes.unacloud.share.queue.messages;

import java.io.StringWriter;

import uniandes.unacloud.share.enums.QueueMessageType;

import org.json.*;

/**
 * Class to represents message to be sent in queue
 * @author CesarF
 */
public class QueueMessage {

	public static final String TYPE_MESSAGE = "Type";
	
	public static final String REQUESTER_MESSAGE = "Requester";
	
	public static final String CONTENT_MESSAGE = "Content";
	
	/**
	 * Message type
	 */
	private QueueMessageType type;	
	/**
	 * Who sent message
	 */
	private String requester;	
	
	/**
	 * Object JSON
	 */
	private JSONObject messageContent;
	
	public QueueMessage() {
		
	}
	
	public QueueMessage(String requester) {
		super();
		this.type = null;
		this.requester = requester;
		this.messageContent = new JSONObject();		
	}
	
	/**
	 * Returns the message in JSON Format
	 * @return message in messageParts
	 */
	public String getMessage(){
		JSONObject total = new JSONObject();
		
		total.put(TYPE_MESSAGE, this.type);
		total.put(REQUESTER_MESSAGE, this.requester);
		total.put(CONTENT_MESSAGE, this.messageContent);
		
		StringWriter out = new StringWriter();
		total.write(out);
		
		String jsonText = out.toString();
		return jsonText;
	}
	
	/**
	 * Read the String and set the variables in JSON Format
	 * @param toParse String of Message
	 */
	public void setMessage(String toParse){
		JSONObject json;
		json = new JSONObject(toParse);
		
		this.requester = json.getString(REQUESTER_MESSAGE);
		this.type = QueueMessageType.getType(json.getString(TYPE_MESSAGE));
		this.messageContent = json.getJSONObject(CONTENT_MESSAGE);
	}
	
	/**
	 * Return the Type of Message
	 * @return type of message
	 */
	public QueueMessageType getType() {
		return type;
	}
	
	/**
	 * Set the type of Message
	 * @param type
	 */
	public void setType(QueueMessageType type) {
		this.type = type;
	}
	
	/**
	 * Return the content of Message in JSON Format
	 * @return Content of Message
	 */
	public JSONObject getMessageContent() {
		return messageContent;
	}
	
	/**
	 * Set the JSON Content of the Message 
	 * @param messageContent
	 */
	public void setMessageContent(JSONObject messageContent) {
		this.messageContent = messageContent;
	}
	
	/**
	 * Return the Requester of the Message
	 * @return requester
	 */
	public String getRequester() {
		return requester;
	}
	
	/**
	 * Set the Requester of the Message
	 * @param requester
	 */
	public void setRequester(String requester) {
		this.requester = requester;
	}
}
