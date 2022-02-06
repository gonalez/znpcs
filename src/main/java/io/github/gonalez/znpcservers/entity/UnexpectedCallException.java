package io.github.gonalez.znpcservers.entity;

/**
 * Exception thrown when an unexpected problem occurs.
 */
public class UnexpectedCallException extends RuntimeException {
    public UnexpectedCallException(String message) {
        super(message);
    }

    public UnexpectedCallException(String message, Exception cause) {
        super(message, cause);
    }
}
