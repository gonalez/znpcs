package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CommandEnvironment implements CommandProvider {
  private final ConfigurationProvider configurationProvider;
  private final ImmutableClassToInstanceMap<Command> commands;

  CommandResult mergedCommandResult;
  Map<Command, CommandResult> executedCommands = new HashMap<>();

  public CommandEnvironment(
      ConfigurationProvider configurationProvider,
      ImmutableClassToInstanceMap<Command> commands) {
    this.configurationProvider = configurationProvider;
    this.commands = commands;
  }

  public ImmutableMap<Command, CommandResult> getExecutedCommands() {
    return ImmutableMap.copyOf(executedCommands);
  }

  public CommandResult getMergedCommandResult() {
    return mergedCommandResult;
  }

  public ConfigurationProvider getConfigurationProvider() {
    return configurationProvider;
  }

  public <C extends Configuration> C getConfig(Class<C> configClass) {
    try {
      return configurationProvider.getConfiguration(configClass);
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public @Nullable Command provideCommand(Class<? extends Command> commandClass) {
    return commands.get(commandClass);
  }
}
