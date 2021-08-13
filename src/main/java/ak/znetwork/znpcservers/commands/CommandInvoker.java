package ak.znetwork.znpcservers.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Used for invoking a command method.
 */
public class CommandInvoker {
    /**
     * The command instance.
     */
    private final Command command;

    /**
     * The command method.
     */
    private final Method commandMethod;

    /**
     * The command permission.
     */
    private final String permission;

    /**
     * Creates a new sub-command invoker for the given command.
     *
     * @param command The command instance.
     * @param commandMethod The command method.
     * @param permission The command permission.
     */
    public CommandInvoker(Command command,
                          Method commandMethod,
                          String permission) {
        this.command = command;
        this.commandMethod = commandMethod;
        this.permission = permission;
    }

    /**
     * Invokes the command.
     *
     * @param sender The command sender to run the command for.
     * @param command The command.
     * @throws CommandPermissionException If commandSender does not have permission to execute the subCommand.
     * @throws CommandExecuteException If subCommand cannot be executed.
     */
    public void execute(CommandSender sender, Object command) throws CommandPermissionException, CommandExecuteException {
        if (permission.length() > 0 && !sender.getCommandSender().hasPermission(permission)) {
            throw new CommandPermissionException("Insufficient permission.");
        }
        try {
            commandMethod.invoke(this.command, sender, command);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CommandExecuteException(e.getMessage(), e.getCause());
        }
    }
}
