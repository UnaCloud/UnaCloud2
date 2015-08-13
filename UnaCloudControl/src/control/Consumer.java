package control;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class Consumer extends Thread{
	
	@Override
	public void run() {
		try {
			startConsumer();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void startConsumer() throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("157.253.236.163");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    channel.queueDeclare(Init.QUEUE_NAME, false, false, false, null);
	    System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
	    QueueingConsumer consumer = new QueueingConsumer(channel);
	    channel.basicConsume(Init.QUEUE_NAME, true, consumer);

	    while (true) {
	      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	      String message = new String(delivery.getBody());
	      System.out.println(" [x] Received '" + message + "'");
	      Thread.sleep(2000);
		}
				
	}

}
