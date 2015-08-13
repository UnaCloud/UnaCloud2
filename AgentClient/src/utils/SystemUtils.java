package utils;

public class SystemUtils {
	/**
	 * sleeps agent tread
	 * @param time sleep time
	 */
	public static void sleep(long time){
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
