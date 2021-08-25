package io.github.znetworkw.znpcservers;

import io.github.znetworkw.znpcservers.npc.NPC;

/**
 * Exception for any errors that occur while managing a {@link NPC}.
 */
public class UnexpectedCallException extends RuntimeException {
    /**
     * @param cause The throwable cause.
     */
    public UnexpectedCallException(Throwable cause) {
        super(cause);
    }
}
