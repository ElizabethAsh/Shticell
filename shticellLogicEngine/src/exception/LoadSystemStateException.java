package exception;

public class LoadSystemStateException extends RuntimeException {
    public LoadSystemStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
