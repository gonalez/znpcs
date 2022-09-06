package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.command.internal.DefaultPluginCommandBuilder;

/**
 * Interface for plugin commands.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginCommand {
    /**
     * Creates a new plugin command builder with the specified name.
     *
     * @param name the command name.
     * @return a new plugin command builder.
     */
    static PluginCommandBuilder builder(String name) {
        return new DefaultPluginCommandBuilder(name);
    }

    void init(PluginCommandExec exec);

    /**
     * The command name.
     *
     * @return command name.
     */
    String getName();

    /**
     *
     * @return
     */
    ImmutableList<PluginSubCommand> getSubCommands();

    interface PluginCommandBuilder {
        PluginCommandBuilder withName(String commandName);
        PluginCommandBuilder withExec(PluginCommandExec exec);
        PluginCommandBuilder addSubCommand(PluginSubCommand subCommand);

        PluginCommand build();
    }
}
