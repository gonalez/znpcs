package io.github.gonalez.znpcservers.npc.function;

/**
 * Exception thrown when the {@link AbstractNpcFunction#validateContext(NpcFunctionContext)} is
 * called and the validation failed.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ValidateNpcFunctionException extends RuntimeException {
    /**
     * Creates the exception with the specified message.
     *
     * @param message the message.
     */
    public ValidateNpcFunctionException(String message) {
        super(message);
    }
}
