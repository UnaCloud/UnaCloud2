package queue;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class QueueRabbitManager extends QueueTaskerConnection{
	
	ConnectionFactory factory ;

	protected QueueRabbitManager(String username, String password, String ip,
			int port, String queueName) {
		super(username, password, ip, port, queueName);
		createFactory();
	}

	@Override
	public void sendMessage(QueueMessage message) {
		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(queueName, false, false, false, null);
			channel.basicPublish("", queueName, null, message.getMessage().getBytes());
			System.out.println(" [x] Sent '" + message.getMessage() + "'");
		} catch (Exception e) {
			e.printStackTrace();
		} 		
	}

	@Override
	public void getMessage(final QueueReader reader) {
		Connection connection = null;
		Channel channel = null;
		try {
			connection = factory.newConnection();
		    channel = connection.createChannel();
		    channel.queueDeclare(queueName, false, false, false, null);
		    Consumer consumer = new DefaultConsumer(channel) {
		    	@Override
		        public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
		            throws IOException {
		          String message = new String(body, "UTF-8");
		          QueueMessage qmessage = new QueueMessage();
		          qmessage.setMessage(message);
		          reader.processMessage(qmessage);
		        }
		    };
		    channel.basicConsume(queueName, true, consumer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Creates a new factory to generate connections to queue
	 */
	private void createFactory(){
		factory = new ConnectionFactory();
		factory.setHost(ip);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
	}

}
