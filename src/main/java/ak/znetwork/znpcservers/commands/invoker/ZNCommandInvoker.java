package ak.znetwork.znpcservers.commands.invoker;

import ak.znetwork.znpcservers.commands.ZNCommand;
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static ak.znetwork.znpcservers.commands.impl.ZNCommandImpl.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ZNCommandInvoker {

    /**
     * The command instance.
     */
    private final ZNCommand command;

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
     * @param command       The command instance.
     * @param commandMethod The command method.
     * @param permission    The command permission.
     */
    public ZNCommandInvoker(ZNCommand command,
                            Method commandMethod,
                            String permission) {
        this.command = command;
        this.commandMethod = commandMethod;
        this.permission = permission;
    }

    /**
     * Invokes the subcommand.
     *
     * @param sender                       The commandSender to run the command.
     * @param object                       The subCommand.
     * @throws CommandPermissionException  If commandSender does not have permission to execute the subCommand.
     * @throws CommandExecuteException     If subCommand cannot be executed.
     */
    public void execute(ZNCommandSender sender, Object object) throws CommandPermissionException, CommandExecuteException {
        if (permission.length() > 0 && !sender.getCommandSender().hasPermission(permission)) {
            throw new CommandPermissionException("Insufficient permission");
        }

        try {
            commandMethod.invoke(command, sender, object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CommandExecuteException(e.getMessage(), e.getCause());
        }
    }
}
