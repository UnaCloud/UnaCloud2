package unacloud.share.queue;

/**
 * Class to represents message to be sent in queue
 * @author Cesar
 *
 */
public class QueueMessage {

	/**
	 * Message type
	 */
	private QueueMessageType type;	
	/**
	 * Who sent message
	 */
	private String requester;	
	/**
	 * Parts of message
	 */
	private String[] messageParts;

	private static final String delimiter = "-";
	
	public QueueMessage() {
		
	}
	
	public QueueMessage(QueueMessageType type, String requester, String[] messageParts) {
		super();
		this.type = type;
		this.requester = requester;
		this.messageParts = messageParts;
	}
	
	/**
	 * Return the message in one String delimited by special character
	 * @return
	 */
	public String getMessage(){
		StringBuilder message = new StringBuilder();
		message.append(type.name()).append(delimiter).append(requester);
		for(String data: messageParts){
			message.append(delimiter).append(data);
		}
		return message.toString();
	}
	
	/**
	 * sets the string message parts array using a String
	 * @param data
	 */
	public void setMessage(String data){
		String[] parts = data.split(delimiter);
		type=QueueMessageType.getType(parts[0]);
		requester=parts[1];
		messageParts = new String[parts.length-2];
		for (int i = 2, j = 0; i < parts.length; i++, j++) {
			messageParts[j] = parts[i];
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public QueueMessageType getType() {
		return type;
	}
	
	/**
	 * 
	 * @return
	 */
	public String[] getMessageParts() {
		return messageParts;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRequester() {
		return requester;
	}
}
