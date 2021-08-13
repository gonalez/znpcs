package ak.znetwork.znpcservers.commands;

/**
 * Thrown when a command is not found.
 */
public class CommandNotFoundException extends Exception {
    /**
     * @param message The exception message.
     */
    public CommandNotFoundException(String message) {
        super(message);
    }
}
