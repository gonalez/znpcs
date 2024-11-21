package io.github.gonalez.znpcs.command;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableClassToInstanceMap;
import javax.annotation.Nullable;

public class SimpleCommandProvider implements CommandProvider {
  private final ImmutableClassToInstanceMap<Command> commands;

  public SimpleCommandProvider(ImmutableClassToInstanceMap<Command> commands) {
    this.commands = Preconditions.checkNotNull(commands);
  }

  @Nullable
  @Override
  public Command provideCommand(Class<? extends Command> commandClass) {
    return commands.get(commandClass);
  }
}
