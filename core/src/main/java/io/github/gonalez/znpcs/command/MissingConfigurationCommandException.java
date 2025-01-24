package io.github.gonalez.znpcs.command;

import io.github.gonalez.znpcs.configuration.Configuration;

public class MissingConfigurationCommandException extends CommandException {
  private final Class<? extends Configuration> missingConfiguration;

  public MissingConfigurationCommandException(
      Command command,
      Class<? extends Configuration> missingConfiguration) {
    super("Missing configuration dependency: " + missingConfiguration, command);
    this.missingConfiguration = missingConfiguration;
  }

  public Class<? extends Configuration> getMissingConfiguration() {
    return missingConfiguration;
  }
}
