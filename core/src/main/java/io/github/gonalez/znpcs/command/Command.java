package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Command {

  public abstract String getName();

  protected abstract CommandResult execute(
      ImmutableList<String> args, CommandEnvironment commandEnvironment);

  protected abstract int getMandatoryArguments();

  protected Collection<Command> getChildren() {
    return ImmutableList.of();
  }

  protected CommandResult newCommandResult() {
    return CommandResult.create(this);
  }

  CommandResult validateCommand(CommandResult commandResult) {
    return commandResult;
  }
}
