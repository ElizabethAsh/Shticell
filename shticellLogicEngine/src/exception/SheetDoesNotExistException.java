package exception;

public class SheetDoesNotExistException extends RuntimeException{
    public SheetDoesNotExistException(String message){ super(message); }
}
