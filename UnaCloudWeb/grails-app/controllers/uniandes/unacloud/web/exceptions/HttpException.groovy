package uniandes.unacloud.web.exceptions

class HttpException extends RuntimeException {

    private int code;

    public HttpException(int code) {
        super();
        this.code=code;
    }

    public HttpException(int code,String message, Throwable cause) {
        super(message, cause);
        this.code=code;

    }

    public HttpException(int code,String message) {
        super(message);
        this.code=code;

    }

    public HttpException(int code,Throwable cause) {
        super(cause);
        this.code=code;

    }

    public int getCode()
    {
        return code;
    }
}
