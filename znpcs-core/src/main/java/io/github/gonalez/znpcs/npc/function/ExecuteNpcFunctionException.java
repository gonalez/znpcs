package io.github.gonalez.znpcs.npc.function;

/**
 * Exception thrown when an function cannot be executed.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ExecuteNpcFunctionException extends RuntimeException {
    /**
     * Creates the exception with the specified message.
     *
     * @param message the message.
     */
    public ExecuteNpcFunctionException(String message) {
        super(message);
    }
}
