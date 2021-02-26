package ak.znetwork.znpcservers.commands;

import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandNotFoundException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;
import ak.znetwork.znpcservers.commands.impl.ZNCommandImpl;
import ak.znetwork.znpcservers.commands.invoker.ZNCommandInvoker;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ZNCommand implements ZNCommandImpl {

    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * The command class instance.
     */
    private final Object commandInstance;

    /**
     * A map that contains the subcommands of the current command.
     */
    private final HashMap<ZNCommandSub, ZNCommandInvoker<? extends CommandSender>> consumerSet;

    /**
     * Creates a new command.
     *
     * @param commandInstance The command class instance.
     */
    public ZNCommand(Object commandInstance) {
        this.commandInstance = commandInstance;
        this.consumerSet = new HashMap<>();

        this.load();
    }

    @Override
    public void load() {
        for (Method method : getCommandInstance().getClass().getMethods()) {
            if (method.isAnnotationPresent(ZNCommandSub.class)) {
                ZNCommandSub cmdInfo = method.getAnnotation(ZNCommandSub.class);
                getConsumerSet().put(cmdInfo, new ZNCommandInvoker<>(getCommandInstance(), method, cmdInfo.permission()));
            }
        }
    }

    @Override
    public <S extends ZNCommandSender> void execute(S commandSender, String[] args) throws CommandNotFoundException, CommandPermissionException, CommandExecuteException {
        Optional<Map.Entry<ZNCommandSub, ZNCommandInvoker<? extends CommandSender>>> subCommandOptional = consumerSet.entrySet().stream().filter(subCommand -> subCommand.getKey().name().contentEquals(args.length > 0 ? args[0] : "")).findFirst();
        if (!subCommandOptional.isPresent())
            throw new CommandNotFoundException("Command not found");

        ZNCommandInvoker<S> command = (ZNCommandInvoker<S>) subCommandOptional.get().getValue();
        command.execute(commandSender, loadArgs(subCommandOptional.get().getKey(), args));
    }

    /**
     * Converts the provided subcommand arguments to a map.
     *
     * @param subCommand The subcommand.
     * @param args       The subcommand arguments.
     * @return           A map with the subcommand arguments for the provided values.
     */
    private Map<String, String> loadArgs(ZNCommandSub subCommand, String[] args) {
        Map<String, String> argsMap = new HashMap<>();
        for (int i = 1; i <= args.length; i++) {
            String input = args[i - 1];

            if (contains(subCommand, input)) {
                StringBuilder value = new StringBuilder();

                for (int text = i; text < args.length;) {
                    if (!contains(subCommand, args[text++])) value.append(args[i++]).append(WHITESPACE);
                    else break;
                }

                argsMap.put(input.replace("-", ""), value.substring(0, Math.max(0, value.length() - 1)));
            }
        }
        return argsMap;
    }

    /**
     * Checks if subcommand exists.
     *
     * @param subCommand The subCommand to check.
     * @param input      The subCommand name.
     * @return           {@code true} If subcommand found.
     */
    private boolean contains(ZNCommandSub subCommand, String input) {
        return Stream.of(subCommand.aliases()).anyMatch(s -> s.equalsIgnoreCase(input));
    }
}
