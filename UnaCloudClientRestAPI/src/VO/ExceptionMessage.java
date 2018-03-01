package VO;

public class ExceptionMessage {
    int status;

    String text;

    public ExceptionMessage(int status, String text) {
        this.status = status;
        this.text = text;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String message) {
        this.text = message;
    }

    public String toString()
    {
        return status+":"+text;
    }
}
