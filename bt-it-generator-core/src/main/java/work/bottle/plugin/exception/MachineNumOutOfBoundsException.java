package work.bottle.plugin.exception;

public class MachineNumOutOfBoundsException extends RuntimeException {
    public MachineNumOutOfBoundsException() {
        super("The range of machine number is 0 ~ 127");
    }

    public MachineNumOutOfBoundsException(String message) {
        super(message);
    }

    public MachineNumOutOfBoundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MachineNumOutOfBoundsException(Throwable cause) {
        super(cause);
    }

    public MachineNumOutOfBoundsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
