package tasks;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExecutorService {
	public static int REQUEST_POOL_THREAD_SIZE = 2;
	public static int BACKGROUND_POOL_THREAD_SIZE = 2;
	/**
     * A pool of threads used to attend UnaCloud server requests
     */
    private static Executor requestPool,backPool;
    public static synchronized void executeRequestTask(Runnable run){
    	if(requestPool==null)requestPool = Executors.newFixedThreadPool(REQUEST_POOL_THREAD_SIZE);
    	requestPool.execute(run);
    }
    public static synchronized void executeBackgroundTask(Runnable run){
    	if(backPool==null)backPool = Executors.newFixedThreadPool(BACKGROUND_POOL_THREAD_SIZE);
    	backPool.execute(run);
    }
    
}
