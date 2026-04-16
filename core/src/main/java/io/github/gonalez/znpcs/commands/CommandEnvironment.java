package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class CommandEnvironment {
  private final ImmutableClassToInstanceMap<Command> commands;
  final Map<Command, CommandResult> executedCommands = new HashMap<>();

  public CommandEnvironment(ImmutableClassToInstanceMap<Command> commands) {
    this.commands = commands;
  }

  public ImmutableMap<Command, CommandResult> getExecutedCommands() {
    return ImmutableMap.copyOf(executedCommands);
  }

  @Nullable
  public Command provideCommand(Class<? extends Command> commandClass) {
    return commands.get(commandClass);
  }
}
