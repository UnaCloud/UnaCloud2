package uniandes.unacloud.control;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;

import uniandes.unacloud.common.utils.UnaCloudConstants;

/**
 * Initial point to start all services from UnaCloud Control
 * @author CesarF
 *
 */
public class InitialPoint {
	
	/**
	 * Method to initialize all services 
	 * @param args
	 */
	public static void main(String[] args) {		
		try {
			try {
	    		//Create agent log file
	        	PrintStream ps = new PrintStream(new FileOutputStream(UnaCloudConstants.CONTROL_OUT_LOG, true), true) {
	        		@Override
	        		public void println(String x) {
	        			super.println(new Date() + " " + x);
	        		}
	        		@Override
	        		public void println(Object x) {
	        			super.println(new Date() + " " + x);
	        		}
	        	};
	        	PrintStream psError = new PrintStream(new FileOutputStream(UnaCloudConstants.CONTROL_ERROR_LOG, true), true) {
	            	@Override
	        		public void println(String x) {
	        			super.println(new Date() + " " + x);
	        		}
	        		@Override
	        		public void println(Object x) {
	        			super.println(new Date() + " " + x);
	        		}
	        	};
				System.setOut(ps);
				System.setErr(psError);
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
