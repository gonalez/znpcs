package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableClassToInstanceMap;
import io.github.gonalez.znpcs.context.Context;
import javax.annotation.Nullable;

public class CommandEnvironment {
  @Nullable private final Context defaultContext;
  private final ImmutableClassToInstanceMap<Command> commands;

  public CommandEnvironment(
      @Nullable Context defaultContext,
      ImmutableClassToInstanceMap<Command> commands) {
    this.defaultContext = defaultContext;
    this.commands = commands;
  }

  public @Nullable Context getDefaultContext() {
    return defaultContext;
  }

  @Nullable
  public Command provideCommand(Class<? extends Command> commandClass) {
    return commands.get(commandClass);
  }
}
