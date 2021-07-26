package ak.znetwork.znpcservers.commands.exception;

/**
 * Thrown when a command is not found.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class CommandNotFoundException extends Exception {
    /**
     * @param message The exception message.
     */
    public CommandNotFoundException(String message) {
        super(message);
    }
}
