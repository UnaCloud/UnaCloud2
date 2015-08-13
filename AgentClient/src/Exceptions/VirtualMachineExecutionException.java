package Exceptions;

/**
 * Exception representing errors on virtual machine operations like configure, start, stop
 * @author Clouder
 */
public class VirtualMachineExecutionException extends Exception{

    private static final long serialVersionUID = 1323530370930637999L;

	public VirtualMachineExecutionException(String message){
		super(message);
        
    }
	public VirtualMachineExecutionException(String message,Exception cause){
		super(message,cause);
    }

}
