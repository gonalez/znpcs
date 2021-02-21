package ak.znetwork.znpcservers.commands.exception;

/**
 * Throw when a command could not be executed.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class CommandExecuteException extends Exception {

    public CommandExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
