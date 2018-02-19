package uniandes.unacloud.web.exceptions
/**
 * Http Exception for customized rest exception handling.
 * @author s.guzmanm
 */
class HttpException extends RuntimeException {
	
    //Http code
    private int code;
    //--------
    //Default constructor of runtime exception with code embedded within.
    //---------
     HttpException(int code) {
        super();
        this.code=code;
    }

     HttpException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code=code;

    }

     HttpException(int code, String message) {
        super(message);
        this.code=code;

    }

     HttpException(int code, Throwable cause) {
        super(cause);
        this.code=code;

    }
    /**
     * Getter for the code.
     * @return code
     */
     int getCode()
    {
        return code;
    }
}
