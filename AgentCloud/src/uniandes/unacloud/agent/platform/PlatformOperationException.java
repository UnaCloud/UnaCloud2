package uniandes.unacloud.agent.platform;

/**
 * Exception to be used by hypervisors to notify hypervisor operations errors.
 * @author Clouder
 */
public class PlatformOperationException extends Exception{
    private static final long serialVersionUID = -7248252531368729009L;

    /**
     * Creates a new exception based in message
     * @param message
     */
	protected PlatformOperationException(String message) {
        super(message);
    }
}
