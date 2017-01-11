package uniandes.unacloud.control.init;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

/**
 * Initial point to start all services from UnaCloud Control
 * @author CesarF
 *
 */
public class Init {
	
	/**
	 * Method to initialize all services 
	 * @param args
	 */
	public static void main(String[] args) {		
		try {
			try {
	    		//Create agent log file
	        	PrintStream ps=new PrintStream(new FileOutputStream("log_control.txt",true),true){
	        		@Override
	        		public void println(String x) {
	        			super.println(new Date()+" "+x);
	        		}
	        		@Override
	        		public void println(Object x) {
	        			super.println(new Date()+" "+x);
	        		}
	        	};
				System.setOut(ps);
				System.setErr(ps);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			ControlManager.getInstance();		
			System.out.println("UnaCloud Control is running");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}		
	}

}
