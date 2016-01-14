package unacloud.task.queue;

public interface QueueReader {

	public void processMessage(QueueMessage message);
}
