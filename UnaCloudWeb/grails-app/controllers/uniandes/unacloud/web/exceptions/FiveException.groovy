package uniandes.unacloud.web.exceptions


class FiveException extends RuntimeException {
    public FiveException() {
        super();
    }
    public FiveException(String message, Throwable cause) {
        super(message, cause);
    }
    public FiveException(String message) {
        super(message);
    }
    public FiveException(Throwable cause) {
        super(cause);
    }

}
