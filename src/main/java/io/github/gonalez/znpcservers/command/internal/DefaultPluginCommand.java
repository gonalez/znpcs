package io.github.gonalez.znpcservers.command.internal;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcservers.command.PluginCommand;
import io.github.gonalez.znpcservers.command.PluginCommandExec;
import io.github.gonalez.znpcservers.command.PluginSubCommand;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginCommand implements PluginCommand {
    private final String commandName;
    private final ImmutableList<PluginSubCommand> subCommands;

    public DefaultPluginCommand(
        String commandName, ImmutableList<PluginSubCommand> subCommands) {
        this.commandName = commandName;
        this.subCommands = subCommands;
    }

    @Override
    public void init(PluginCommandExec exec) {
        exec.init();
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public ImmutableList<PluginSubCommand> getSubCommands() {
        return subCommands;
    }
}
