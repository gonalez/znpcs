package io.github.gonalez.znpcs.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationProvider;
import javax.annotation.concurrent.Immutable;

/** Wrapper for common command operations. */
@Immutable
public final class CommandHooks {
  private final Command command;
  private final ImmutableList<String> args;
  private final CommandEnvironment commandEnvironment;

  public CommandHooks(
      Command command,
      ImmutableList<String> args,
      CommandEnvironment commandEnvironment) {
    this.command = checkNotNull(command);
    this.args = checkNotNull(args);
    this.commandEnvironment = checkNotNull(commandEnvironment);
  }

  public ImmutableList<String> getArgs() {
    return args;
  }

  public <C extends Configuration> C getConfigurationOrThrow(
      Class<? extends C> configDependency) throws MissingConfigurationCommandException {
    ConfigurationProvider configurationProvider = commandEnvironment.getConfigurationProvider();
    Configuration maybeGetConfig = configurationProvider.provideConfiguration(configDependency);
    if (configDependency.isInstance(maybeGetConfig)) {
      return configDependency.cast(maybeGetConfig);
    }
    throw new MissingConfigurationCommandException(command, configDependency);
  }
}
