package io.github.gonalez.znpcs.command.internal;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.PluginCommand;
import io.github.gonalez.znpcs.command.PluginCommandExec;
import io.github.gonalez.znpcs.command.PluginSubCommand;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginCommandBuilder implements PluginCommand.PluginCommandBuilder {
    private static final ImmutableList<PluginSubCommand> EMPTY_LIST =
        ImmutableList.of();

    private final String commandName;
    private final ImmutableList<PluginSubCommand> subCommands;

    private DefaultPluginCommandBuilder(
        String commandName, ImmutableList<PluginSubCommand> subCommands) {
        this.commandName = commandName;
        this.subCommands = subCommands;
    }

    public DefaultPluginCommandBuilder(String commandName) {
        this(commandName, EMPTY_LIST);
    }

    public ImmutableList<PluginSubCommand> getSubCommands() {
        return subCommands;
    }

    @Override
    public PluginCommand.PluginCommandBuilder withName(String commandName) {
        return new DefaultPluginCommandBuilder(commandName, subCommands);
    }

    @Override
    public PluginCommand.PluginCommandBuilder addSubCommand(PluginSubCommand subCommand) {
        return new DefaultPluginCommandBuilder(commandName, ImmutableList.<PluginSubCommand>builder()
            .addAll(subCommands).add(subCommand).build());
    }

    @Override
    public PluginCommand.PluginCommandBuilder withExec(PluginCommandExec exec) {
        return new DefaultPluginCommandBuilder(commandName, subCommands);
    }

    @Override
    public PluginCommand build() {
        return new DefaultPluginCommand(commandName, subCommands);
    }
}
