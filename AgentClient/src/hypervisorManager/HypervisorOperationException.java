package hypervisorManager;

/**
 * Exception to be used by hypervisors to notify hypervisor operations errors.
 * @author Clouder
 */
public class HypervisorOperationException extends Exception{
    private static final long serialVersionUID = -7248252531368729009L;

	protected HypervisorOperationException(String message) {
        super(message);
    }
}
