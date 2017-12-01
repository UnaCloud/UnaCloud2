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
	public static final int BACKGROUND_POOL_THREAD_SIZE = 2;
	
	/**
     * A pool of threads used to attend UnaCloud server task
     */    
    private static Executor backPool;
    
    /**
     * Executes a new runnable entity 
     * @param run
     */
    public static synchronized void executeBackgroundTask(Runnable run) {
    	if (backPool == null)
    		backPool = Executors.newFixedThreadPool(BACKGROUND_POOL_THREAD_SIZE);
    	backPool.execute(run);
    }
    
}
