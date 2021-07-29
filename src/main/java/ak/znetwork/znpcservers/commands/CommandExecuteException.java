package ak.znetwork.znpcservers.commands;

/**
 * Throw when a command could not be executed.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class CommandExecuteException extends Exception {
    /**
     * @param message The exception message.
     * @param cause   The throwable cause.
     */
    public CommandExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
