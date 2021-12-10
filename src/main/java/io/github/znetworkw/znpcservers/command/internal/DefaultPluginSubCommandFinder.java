package io.github.znetworkw.znpcservers.command.internal;

import io.github.znetworkw.znpcservers.command.PluginSubCommand;
import io.github.znetworkw.znpcservers.command.PluginSubCommandFinder;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginSubCommandFinder implements PluginSubCommandFinder {
    public static final PluginSubCommandFinder INSTANCE = new DefaultPluginSubCommandFinder();

    @Override
    public PluginSubCommand find(String context, Iterable<PluginSubCommand> subCommands) {
        for (PluginSubCommand pluginSubCommand : subCommands) {
            if (context.equalsIgnoreCase(pluginSubCommand.name())) {
                return pluginSubCommand;
            }
        }
        return null;
    }
}
