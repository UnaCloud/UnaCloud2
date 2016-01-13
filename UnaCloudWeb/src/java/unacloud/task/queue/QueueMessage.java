package unacloud.task.queue;

public class QueueMessage {

	private String type;	
	private String requester;	
	private String[] messageParts;

	private static final String delimiter = "-";
	
	public QueueMessage(String type, String requester, String[] messageParts) {
		super();
		this.type = type;
		this.requester = requester;
		this.messageParts = messageParts;
	}
	
	public String getMessage(){
		StringBuilder message = new StringBuilder();
		message.append(type).append(delimiter).append(requester);
		for(String data: messageParts){
			message.append(delimiter).append(data);
		}
		return message.toString();
	}
}
