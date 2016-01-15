package queue;

public class QueueMessage {

	private QueueMessageType type;	
	private String requester;	
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
	
	public String getMessage(){
		StringBuilder message = new StringBuilder();
		message.append(type.name()).append(delimiter).append(requester);
		for(String data: messageParts){
			message.append(delimiter).append(data);
		}
		return message.toString();
	}
	
	public void setMessage(String data){
		String[] parts = data.split(delimiter);
		type=QueueMessageType.getType(parts[0]);
		requester=parts[1];
		messageParts = new String[parts.length-2];
		for (int i = 2, j = 0; i < parts.length; i++, j++) {
			messageParts[j] = parts[i];
		}
	}
}
