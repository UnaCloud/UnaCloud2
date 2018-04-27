package uniandes.unacloud.agent.execution;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * To manage thread pools to execute tasks
 * @author CesarF
 *
 */
public class ExecutorService {
	
	/**
	 * Quantity of threads to process tasks in background
	 */
	//TODO it must be configured using the number of physical cores
	public static final int BACKGROUND_POOL_THREAD_SIZE = 4;
	
	/**
	 * Quantity of threads to process request from server
	 */
	public static final int REQUEST_POOL_THREAD_SIZE = 4;
	
	/**
     * A pool of threads used to manage execution batch processes
     */    
    private static Executor backPool;
    
    /**
     * A pool of threads used to manage file processes
     */
    private static Executor requestPool;
    
    /**
     * Executes a new runnable entity 
     * @param run
     */
    public static synchronized void executeBackgroundTask(Runnable run) {
		System.out.println("Background: Executing task is back pool");
		if (backPool == null)
    		backPool = Executors.newFixedThreadPool(BACKGROUND_POOL_THREAD_SIZE);
    	backPool.execute(run);
    }
    
    /**
     * Executes a new runnable entity 
     * @param run
     */
    public static synchronized void executeRequestTask(Runnable run){
    	if(requestPool==null)requestPool = Executors.newFixedThreadPool(REQUEST_POOL_THREAD_SIZE);
    	requestPool.execute(run);
    }
    
}
