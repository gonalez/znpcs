package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Command {

  public abstract String getName();

  protected abstract CommandResult execute(
      CommandProvider commandProvider, ImmutableList<String> args);

  protected abstract int getMandatoryArguments();

  protected Collection<Command> getChildren() {
    return ImmutableList.of();
  }

  public CommandResult executeCommand(
      CommandProvider commandProvider, ImmutableList<String> args) {
    CommandResult validateCommandResult = validateCommand(args);
    if (validateCommandResult.hasError()) {
      return validateCommandResult;
    }
    List<Command> possibleCommands = new ArrayList<>();
    for (int i = 0; i < args.size(); i++) {
      Iterable<Command> commands = (i == 0)
          ? getChildren()
          : Iterables.concat(Iterables.transform(possibleCommands, Command::getChildren));
      for (Command command : ImmutableList.copyOf(commands)) {
        if (command.getName().startsWith(args.get(i))) {
          possibleCommands.add(command);
        }
      }
    }
    Command command = possibleCommands.isEmpty()
        ? this : possibleCommands.get(possibleCommands.size() - 1);
    return command.execute(commandProvider, args);
  }

  protected CommandResult newCommandResult() {
    return CommandResult.create(this);
  }

  CommandResult validateCommand(ImmutableList<String> args) {
    CommandResult commandResult = newCommandResult();
    if (args.size() < getMandatoryArguments()) {
      commandResult.setErrorMessage("not enough args");
    }
    return commandResult;
  }
}
