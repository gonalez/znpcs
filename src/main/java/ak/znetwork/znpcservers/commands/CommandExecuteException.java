package ak.znetwork.znpcservers.commands;

/**
 * Thrown when a command could not be executed.
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
