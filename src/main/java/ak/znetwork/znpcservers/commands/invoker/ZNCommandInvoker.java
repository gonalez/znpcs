package ak.znetwork.znpcservers.commands.invoker;

import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.Getter;

import static ak.znetwork.znpcservers.commands.impl.ZNCommandImpl.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ZNCommandInvoker<S extends ZNCommandSender> {

    /**
     * The command class instance.
     */
    private final Object commandInstance;

    /**
     * The command method.
     */
    private final Method commandMethod;

    /**
     * The command permission.
     */
    private final String permission;

    /**
     * Executes a subCommand
     *
     * @param commandInstance The command class instance.
     * @param commandMethod   The command method.
     * @param permission      The command permission.
     */
    public ZNCommandInvoker(Object commandInstance,
                            Method commandMethod,
                            String permission) {
        this.commandInstance = commandInstance;
        this.commandMethod = commandMethod;
        this.permission = permission;
    }

    /**
     * Invokes a subcommand.
     *
     * @param sender                       The commandSender to run the command.
     * @param object                       The subCommand.
     * @throws CommandPermissionException  If commandSender does not have permission to execute the subCommand.
     * @throws CommandExecuteException     If subCommand cannot be executed.
     */
    public void execute(S sender, Object object) throws CommandPermissionException, CommandExecuteException {
        if (getPermission().length() > 0 && !sender.getCommandSender().hasPermission(getPermission()))
            throw new CommandPermissionException("Insufficient permission");

        try {
            getCommandMethod().invoke(getCommandInstance(), sender, object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new CommandExecuteException(e.getMessage(), e.getCause());
        }
    }
}
