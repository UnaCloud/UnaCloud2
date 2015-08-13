package control;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer extends Thread{
	
	@Override
	public void run() {
		try {
			startProducer();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void startProducer() throws IOException, TimeoutException, InterruptedException {
		
		 ConnectionFactory factory = new ConnectionFactory();
		 factory.setHost("157.253.236.163");
		 Connection connection = factory.newConnection();
		 Channel channel = connection.createChannel();
		 channel.queueDeclare(Init.QUEUE_NAME, false, false, false, null);
		 String message = "Hello World! "+new Date();
		 int i = 0;
		 while(i<100){			
			 channel.basicPublish("", Init.QUEUE_NAME, null, message.getBytes());
			 System.out.println(" [x] Sent '" + message + "'");
			 Thread.sleep(1000);
			 i++;
		 }	
		 channel.close();
		 connection.close();
		
	}

}
