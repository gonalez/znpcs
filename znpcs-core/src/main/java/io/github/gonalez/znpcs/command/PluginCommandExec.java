package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.internal.BukkitPluginCommandExec;
import org.bukkit.command.CommandSender;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginCommandExec {
    static PluginCommandExec of(PluginCommand pluginCommand, PluginSubCommandFinder subCommandFinder) {
        return of(pluginCommand.getName(), pluginCommand.getSubCommands(), subCommandFinder);
    }

    static PluginCommandExec of(
        String name, ImmutableList<PluginSubCommand> pluginSubCommands,
        PluginSubCommandFinder subCommandFinder) {
        return new BukkitPluginCommandExec(name, pluginSubCommands, subCommandFinder);
    }

    void init();

    boolean execute(CommandSender commandSender, String arg, String[] args);

    ImmutableList<PluginSubCommand> subCommands();
    PluginSubCommandFinder getSubCommandFinder();
}
