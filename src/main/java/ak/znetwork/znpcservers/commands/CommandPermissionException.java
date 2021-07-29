package ak.znetwork.znpcservers.commands;

/**
 * Thrown when a sender has no permission to execute command.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class CommandPermissionException extends Exception {
    /**
     * @param message The exception message.
     */
    public CommandPermissionException(String message) {
        super(message);
    }
}
