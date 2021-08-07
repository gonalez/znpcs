package ak.znetwork.znpcservers.commands;

import ak.znetwork.znpcservers.configuration.ConfigValue;
import ak.znetwork.znpcservers.configuration.ConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.types.ClassTypes;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class Command extends BukkitCommand {
    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * The bukkit command map instance.
     */
    private static final CommandMap COMMAND_MAP;

    static {
        try {
            COMMAND_MAP = (CommandMap) ClassTypes.BUKKIT_COMMAND_MAP.get(Bukkit.getServer());
        } catch (IllegalAccessException exception) {
            // Should not happen....
            throw new IllegalStateException("Cannot access command map.");
        }
    }

    /**
     * A map that contains the subcommands for the current command.
     */
    private final Map<CommandInformation, CommandInvoker> subCommands;

    /**
     * Creates a new command.
     */
    public Command(String command) {
        super(command);
        subCommands = new HashMap<>();
        load();
    }

    /**
     * Loads the command.
     */
    private void load() {
        // Register the command
        COMMAND_MAP.register(getName(), this);
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(CommandInformation.class)) {
                CommandInformation cmdInfo = method.getAnnotation(CommandInformation.class);
                subCommands.put(cmdInfo, new CommandInvoker(this, method, cmdInfo.permission()));
            }
        }
    }

    /**
     * Converts the provided subcommand arguments to a map.
     *
     * @param subCommand The subcommand.
     * @param args       The subcommand arguments.
     * @return A map with the subcommand arguments for the provided values.
     */
    private Map<String, String> loadArgs(CommandInformation subCommand,
                                         Iterable<String> args) {
        int size = Iterables.size(args);
        int subCommandsSize = subCommand.aliases().length;
        Map<String, String> argsMap = new HashMap<>();
        if (size > 1) {
            if (subCommand.isMultiple()) {
                argsMap.put(Iterables.get(args, 1), String.join(WHITESPACE, Iterables.skip(args, 2)));
            } else {
                for (int i = 0; i < Math.min(subCommandsSize, size); i++) {
                    int fixedLength = i + 1;
                    if (size > fixedLength) {
                        String input = Iterables.get(args, fixedLength);
                        if (fixedLength == subCommandsSize) {
                            input = String.join(WHITESPACE, Iterables.skip(args, subCommandsSize));
                        }
                        argsMap.put(subCommand.aliases()[i], input);
                    }
                }
            }
        }
        return argsMap;
    }

    /**
     * Returns a set containing the subcommands on the map.
     *
     * @return A set containing the subcommands on the map.
     */
    public Set<CommandInformation> getCommands() {
        return subCommands.keySet();
    }

    @Override
    public boolean execute(org.bukkit.command.CommandSender sender, String commandLabel, String[] args) {
        Optional<Map.Entry<CommandInformation, CommandInvoker>> subCommandOptional =
                subCommands.entrySet().stream()
                        .filter(command -> command.getKey().name().contentEquals(args.length > 0 ? args[0] : ""))
                        .findFirst();

        if (!subCommandOptional.isPresent()) {
            // sub-command not found
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender, ConfigValue.COMMAND_NOT_FOUND);
            return false;
        }

        try {
            Map.Entry<CommandInformation, CommandInvoker> subCommand = subCommandOptional.get();
            subCommand.getValue().execute(new CommandSender(sender), loadArgs(subCommand.getKey(), Arrays.asList(args)));
        } catch (CommandExecuteException e) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender, ConfigValue.COMMAND_ERROR);

            // Logs enabled
            e.printStackTrace();
        } catch (CommandPermissionException e) {
            ConfigManager.getByType(ConfigType.MESSAGES).sendMessage(sender, ConfigValue.NO_PERMISSION);
        }
        return true;
    }
}
