package ak.znetwork.znpcservers.commands;

/**
 * Thrown when a sender has no permission to execute command.
 */
public class CommandPermissionException extends Exception {
    /**
     * @param message The exception message.
     */
    public CommandPermissionException(String message) {
        super(message);
    }
}
