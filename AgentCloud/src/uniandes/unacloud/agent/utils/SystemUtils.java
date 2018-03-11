package uniandes.unacloud.agent.utils;

import java.util.Date;

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
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return date in format yyyy-MM-dd-hh-mm-ss
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getStringDate(){
		Date d = new Date();
		return + (d.getYear() + 1900) + "-" + (d.getMonth() + 1) + "-" + (d.getDate()) + "-" + d.getHours() + "-" + d.getMinutes() + "-" + d.getSeconds();
	}
}
