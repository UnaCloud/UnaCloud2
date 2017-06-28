package uniandes.unacloud.share.queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import uniandes.unacloud.share.queue.messages.QueueMessage;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

/**
 * Extends from Queue Tasker connection, manage connection to RabbitMQ service
 * @author CesarF
 *
 */
public class QueueRabbitManager extends QueueTaskerConnection{
	
	/**
	 * Factory of connection to rabbit
	 */
	ConnectionFactory factory;
	
	Connection connection;

	public QueueRabbitManager(String username, String password, String ip,
			int port, String queueName) throws IOException, TimeoutException {
		super(username, password, ip, port, queueName);
		createFactory();
	}

	@Override
	public void sendMessage(QueueMessage message) {
		Channel channel = null;
		try {
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
		Channel channel = null;
		try {			
		    channel = connection.createChannel();
		    channel.queueDeclare(queueName, false, false, false, null);
		    Consumer consumer = new DefaultConsumer(channel) {
		    	@Override
		        public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties, byte[] body)
		            throws IOException {
		          String message = new String(body, "UTF-8");
		          QueueMessage qmessage = new QueueMessage();
		          qmessage.setMessage(message);
		          try {
		        	  Thread.sleep(1000);
		          } catch (Exception e) {
		        	  
		          }
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
	 * @throws TimeoutException 
	 * @throws IOException 
	 */
	private void createFactory() throws IOException, TimeoutException {
		factory = new ConnectionFactory();
		factory.setHost(ip);
		factory.setPort(port);
		factory.setUsername(username);
		factory.setPassword(password);
		connection = factory.newConnection();
	}

}
