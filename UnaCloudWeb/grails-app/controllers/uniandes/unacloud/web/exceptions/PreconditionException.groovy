package uniandes.unacloud.web.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class PreconditionException extends RuntimeException {
    public PreconditionException() {
        super();
    }
    public PreconditionException(String message, Throwable cause) {
        super(message, cause);
    }
    public PreconditionException(String message) {
        super(message);
    }
    public PreconditionException(Throwable cause) {
        super(cause);
    }
}
