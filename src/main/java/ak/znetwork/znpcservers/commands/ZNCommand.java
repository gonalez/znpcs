package ak.znetwork.znpcservers.commands;

import ak.znetwork.znpcservers.commands.exception.CommandExecuteException;
import ak.znetwork.znpcservers.commands.exception.CommandPermissionException;
import ak.znetwork.znpcservers.commands.impl.ZNCommandImpl;
import ak.znetwork.znpcservers.commands.invoker.ZNCommandInvoker;
import ak.znetwork.znpcservers.configuration.enums.ZNConfigValue;
import ak.znetwork.znpcservers.configuration.enums.type.ZNConfigType;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.types.ClassTypes;

import org.bukkit.Bukkit;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class ZNCommand extends BukkitCommand implements ZNCommandImpl {

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
    private final Map<ZNCommandSub, ZNCommandInvoker> subCommands;

    /**
     * Creates a new command.
     */
    public ZNCommand(String command) {
        super(command);
        subCommands = new HashMap<>();
        load();
    }

    @Override
    public void load() {
        // Register the command
        COMMAND_MAP.register(getName(), this);

        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(ZNCommandSub.class)) {
                ZNCommandSub cmdInfo = method.getAnnotation(ZNCommandSub.class);
                subCommands.put(cmdInfo, new ZNCommandInvoker(this, method, cmdInfo.permission()));
            }
        }
    }

    /**
     * Checks if a subcommand exists.
     *
     * @param subCommand The subCommand to check.
     * @param input      The subCommand name.
     * @return {@code true} If subcommand found.
     */
    private boolean contains(ZNCommandSub subCommand, String input) {
        return Arrays.stream(subCommand.aliases()).anyMatch(input::equalsIgnoreCase);
    }

    /**
     * Converts the provided subcommand arguments to a map.
     *
     * @param subCommand The subcommand.
     * @param args       The subcommand arguments.
     * @return A map with the subcommand arguments for the provided values.
     */
    private Map<String, String> loadArgs(ZNCommandSub subCommand, String[] args) {
        Map<String, String> argsMap = new HashMap<>();
        for (int i = 1; i <= args.length; i++) {
            String input = args[i - 1];
            if (contains(subCommand, input)) {
                StringBuilder value = new StringBuilder();
                for (int text = i; text < args.length; ) {
                    if (!contains(subCommand, args[text++])) {
                        value.append(args[i++]).append(WHITESPACE);
                    } else {
                        break;
                    }
                }
                argsMap.put(input.replace("-", ""), value.substring(0, Math.max(0, value.length() - 1)));
            }
        }
        return argsMap;
    }

    /**
     * Returns a set containing the subcommands on the map.
     *
     * @return A set containing the subcommands on the map.
     */
    public Set<ZNCommandSub> getCommands() {
        return subCommands.keySet();
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Optional<Map.Entry<ZNCommandSub, ZNCommandInvoker>> subCommandOptional = subCommands.entrySet().stream().filter(subCommand -> subCommand.getKey().name().contentEquals(args.length > 0 ? args[0] : "")).findFirst();
        if (!subCommandOptional.isPresent()) {
            // Sub-command not found
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.COMMAND_NOT_FOUND);
            return false;
        }

        try {
            subCommandOptional.get().getValue().execute(new ZNCommandSender(sender), loadArgs(subCommandOptional.get().getKey(), args));
        } catch (CommandExecuteException e) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.COMMAND_ERROR);

            // Logs enabled
            e.printStackTrace();
        } catch (CommandPermissionException e) {
            ConfigManager.getByType(ZNConfigType.MESSAGES).sendMessage(sender, ZNConfigValue.NO_PERMISSION);
        }
        return true;

    }
}
