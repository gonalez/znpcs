package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.znpcs.config.Config;
import io.github.gonalez.znpcs.config.ConfigFactory;
import io.github.gonalez.znpcs.config.ConfigProvider;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class CommandEnvironment {
  private final ConfigProvider configProvider;
  private final ImmutableClassToInstanceMap<Command> commands;
  final Map<Command, CommandResult> executedCommands = new HashMap<>();
  @Nullable CommandResult mergedCommandResult;

  public CommandEnvironment(
      ConfigProvider configProvider,
      ImmutableClassToInstanceMap<Command> commands) {
    this.configProvider = configProvider;
    this.commands = commands;
  }

  public ConfigProvider getConfigProvider() {
    return configProvider;
  }

  public ImmutableMap<Command, CommandResult> getExecutedCommands() {
    return ImmutableMap.copyOf(executedCommands);
  }

  public CommandResult getMergedCommandResult() {
    return mergedCommandResult;
  }

  // shortcut for configProvider
  public <C extends Config> C getConfig(Class<C> configClass) {
    return configProvider.getConfig(configClass);
  }

  @Nullable
  public Command provideCommand(Class<? extends Command> commandClass) {
    return commands.get(commandClass);
  }
}
