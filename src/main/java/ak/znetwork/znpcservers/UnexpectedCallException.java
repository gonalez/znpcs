package ak.znetwork.znpcservers;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 26/7/2021
 */
public class UnexpectedCallException extends RuntimeException {
    /**
     * @param message The exception message.
     * @param cause   The throwable cause.
     */
    public UnexpectedCallException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message The exception message.
     */
    public UnexpectedCallException(String message) {
        super(message);
    }

    /**
     * @param cause The throwable cause.
     */
    public UnexpectedCallException(Throwable cause) {
        super(cause);
    }
}
