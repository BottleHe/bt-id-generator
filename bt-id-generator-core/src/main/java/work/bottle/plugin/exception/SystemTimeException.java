package work.bottle.plugin.exception;

public class SystemTimeException extends RuntimeException {
    public SystemTimeException() {
        super("System time is slower than previous value");
    }

    public SystemTimeException(String message) {
        super(message);
    }

    public SystemTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemTimeException(Throwable cause) {
        super(cause);
    }

    public SystemTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
