package uniandes;

import queue.QueueRabbitManager;
import uniandes.queue.QueueMessageReceiver;
import utils.ConfigurationReader;

/**
 * Start class to initialize all services from UnaCloud Control
 * @author Cesar
 *
 */
public class Init {
	
	/**
	 * Method to initialize all services 
	 * @param args
	 */
	public static void main(String[] args) {
		ConfigurationReader reader  = null;
		try {
			reader = new ConfigurationReader("controlConfig.properties", ControlVariables.list());
			QueueRabbitManager rabbitManager = new QueueRabbitManager(reader.getStringVariable(ControlVariables.QUEUE_USERNAME),
					reader.getStringVariable(ControlVariables.QUEUE_PASS), reader.getStringVariable(ControlVariables.QUEUE_URL), 
					reader.getIntegerVariable(ControlVariables.QUEUE_PORT), "AGENT_CONTROL");
			QueueMessageReceiver.getInstance().createConnection(rabbitManager);
			QueueMessageReceiver.getInstance().startReceiver();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}		
	}

}
