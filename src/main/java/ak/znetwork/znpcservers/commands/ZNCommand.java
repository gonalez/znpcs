package ak.znetwork.znpcservers.commands;

import ak.znetwork.znpcservers.commands.annotation.SubCommand;
import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandNotFoundException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;
import ak.znetwork.znpcservers.commands.invoker.CommandInvoker;
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
public class ZNCommand {

    /**
     * The command class instance.
     */
    private final Object commandInstance;

    /**
     * A map that contains the subcommands of the current command.
     */
    private final HashMap<SubCommand, CommandInvoker<? extends CommandSender>> consumerSet;

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

    /**
     * Loads the command (subcommands).
     */
    public void load() {
        for (Method method : getCommandInstance().getClass().getMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                SubCommand cmdInfo = method.getAnnotation(SubCommand.class);
                getConsumerSet().put(cmdInfo, new CommandInvoker<>(getCommandInstance(), method, cmdInfo.permission()));
            }
        }
    }

    /**
     * Executes a subcommand for sender.
     *
     * @param commandSender               The commandSender to run the command-
     * @param args                        The command arguments.
     * @throws CommandPermissionException If commandSender does not have permission to execute the subCommand.
     * @throws CommandExecuteException    If subCommand cannot be executed.
     * @throws CommandNotFoundException   If no subCommand was found.
     */
    public <T extends CommandSender> void execute(T commandSender, String[] args) throws CommandPermissionException, CommandExecuteException, CommandNotFoundException {
        Optional<Map.Entry<SubCommand, CommandInvoker<? extends CommandSender>>> entryOptional = consumerSet.entrySet().stream().filter(subCommand -> subCommand.getKey().required().contentEquals(args.length > 0 ? args[0] : "")).findFirst();
        if (!entryOptional.isPresent()) throw new CommandNotFoundException("Command not found...");

        CommandInvoker<T> command = (CommandInvoker<T>) entryOptional.get().getValue();
        command.execute(commandSender, loadArgs(entryOptional.get().getKey(), args));
    }

    /**
     * Converts the provided subcommand arguments to a map.
     *
     * @param subCommand The subcommand.
     * @param args       The subcommand arguments.
     * @return           A map with the subcommand arguments for the provided values.
     */
    public Map<String, String> loadArgs(SubCommand subCommand, String[] args) {
        Map<String, String> argsMap = new HashMap<>();
        for (int i = 1; i <= args.length; i++) {
            String input = args[i - 1];

            if (contains(subCommand, input)) {
                StringBuilder value = new StringBuilder();

                for (int text = (i); text < args.length; ) {
                    if (!contains(subCommand, args[text++])) value.append(args[i++]).append(" ");
                    else break;
                }

                argsMap.put(input.replace("-", ""), (value.toString()));
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
    public boolean contains(SubCommand subCommand, String input) {
        return Stream.of(subCommand.aliases()).anyMatch(s -> s.equalsIgnoreCase(input));
    }
}
