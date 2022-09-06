package io.github.gonalez.znpcs.command.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.command.PluginCommandExec;
import io.github.gonalez.znpcs.command.PluginSubCommand;
import io.github.gonalez.znpcs.command.PluginSubCommandFinder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class BukkitPluginCommandExec extends Command implements PluginCommandExec {
    private static final CommandMap COMMAND_MAP;
    static {
        try {
            COMMAND_MAP = (CommandMap) CacheRegistry.BUKKIT_COMMAND_MAP.get(Bukkit.getServer());
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("can't access bukkit command map.");
        }
    }

    private final ImmutableList<PluginSubCommand> subCommands;
    private final PluginSubCommandFinder subCommandFinder;

    public BukkitPluginCommandExec(
        String name, ImmutableList<PluginSubCommand> subCommands,
        PluginSubCommandFinder subCommandFinder) {
        super(name);
        this.subCommands = subCommands;
        this.subCommandFinder = subCommandFinder;
    }

    @Override
    public void init() {
        COMMAND_MAP.register(getName(), this);
    }

    @Override
    public ImmutableList<PluginSubCommand> subCommands() {
        return subCommands;
    }

    @Override
    public PluginSubCommandFinder getSubCommandFinder() {
        return subCommandFinder;
    }

    @Override
    public boolean execute(CommandSender commandSender, String arg, String[] args) {
        final PluginSubCommand subCommand = subCommandFinder.find(args[0], subCommands);
        if (subCommand.permission().length() > 0 && !commandSender.hasPermission(subCommand.permission())) {
            return false;
        } try {
            final Map<String, String> loadArgs = loadArgs(subCommand, ImmutableList.copyOf(args));
            if (loadArgs.size() < subCommand.args().length) {
                commandSender.sendMessage("Few arguments.");
                return false;
            }
            subCommand.execute(commandSender, loadArgs);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    static Map<String, String> loadArgs(PluginSubCommand subCommand, ImmutableList<String> args) {
        final int size = args.size();
        final int subCommandsSize = subCommand.args().length;
        final Map<String, String> argsMap = new HashMap<>();
        if (size > 1) {
            for (int i = 0; i < Math.min(subCommandsSize, size); i++) {
                int fixedLength = i + 1;
                if (size > fixedLength) {
                    String input = args.get(fixedLength);
                    if (fixedLength == subCommandsSize) {
                        input = String.join("", Iterables.skip(args, subCommandsSize));
                    }
                    argsMap.put(subCommand.args()[i], input);
                }
            }
        }
        return argsMap;
    }
}
