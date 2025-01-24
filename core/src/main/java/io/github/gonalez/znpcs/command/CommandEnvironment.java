package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.configuration.ConfigurationProvider;
import javax.annotation.Nullable;

public interface CommandEnvironment {
  CommandResult executeCommand(
      Command command,
      ImmutableList<String> args);

  @Nullable Command getKnownCommand(Class<? extends Command> commandClass);

  ConfigurationProvider getConfigurationProvider();
}
