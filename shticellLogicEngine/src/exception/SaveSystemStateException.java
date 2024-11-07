package exception;

public class SaveSystemStateException extends RuntimeException {
    public SaveSystemStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
