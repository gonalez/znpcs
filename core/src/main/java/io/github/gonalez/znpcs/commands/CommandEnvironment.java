package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.github.gonalez.znpcs.metadata.Metadata;
import javax.annotation.Nullable;

public class CommandEnvironment {
  @Nullable private final Metadata defaultContext;
  private final ImmutableClassToInstanceMap<Command> commands;

  public CommandEnvironment(
      @Nullable Metadata defaultContext, ImmutableClassToInstanceMap<Command> commands) {
    this.defaultContext = defaultContext;
    this.commands = commands;
  }

  public @Nullable Metadata getDefaultContext() {
    return defaultContext;
  }

  @Nullable
  public Command provideCommand(Class<? extends Command> commandClass) {
    return commands.get(commandClass);
  }
}
