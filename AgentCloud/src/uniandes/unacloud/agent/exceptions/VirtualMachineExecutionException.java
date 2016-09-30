package uniandes.unacloud.agent.exceptions;

/**
 * Represent errors on virtual machine operations like configure, start, stop
 * @author Clouder
 */
public class VirtualMachineExecutionException extends Exception{

    private static final long serialVersionUID = 1323530370930637999L;
    /**
	 * Constructor method, creates a new Exception base in message
	 * @param message
	 * @param cause
	 */
	public VirtualMachineExecutionException(String message){
		super(message);
        
    }
	/**
	 * Constructor method, creates a new Exception base in message and cause of exception
	 * @param message
	 * @param cause
	 */
	public VirtualMachineExecutionException(String message,Exception cause){
		super(message,cause);
    }

}
