package uniandes.unacloud.agent.utils;

/**
 * Utility class to manage sleep process
 * @author Clouder
 *
 */
public class SystemUtils {
	/**
	 * sleeps agent tread
	 * @param time sleep time
	 */
	public static void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
