package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableList;
import javax.annotation.Nullable;

public interface CommandEnvironment {
  CommandResult executeCommand(
      Command command,
      ImmutableList<String> args);

  @Nullable Command getKnownConfiguration(Class<? extends Command> configClass);
}
