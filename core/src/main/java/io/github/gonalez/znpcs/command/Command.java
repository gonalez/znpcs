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
      Iterable<Command> commands;
      if (i == 0) {
        commands = getChildren();
      } else {
        commands = Iterables.concat(Iterables.transform(possibleCommands, Command::getChildren));
      }
      for (Command command : ImmutableList.copyOf(commands)) {
        if (command.getName().startsWith(args.get(i))) {
          possibleCommands.add(command);
        }
      }
    }
    Command command = Iterables.getLast(possibleCommands, this);
    return command.execute(commandProvider, args);
  }

  protected CommandResult newCommandResult() {
    return CommandResult.create(this);
  }

  CommandResult validateCommand(ImmutableList<String> args) {
    CommandResult commandResult = newCommandResult();
    int mandatoryArguments = getMandatoryArguments();
    // Check if we have enough arguments for this command
    if (args.size() < mandatoryArguments) {
      commandResult.setErrorMessage(
          String.format(
              "Not enough arguments: expected at least %d, but got %d.",
              mandatoryArguments, args.size()));
    }
    return commandResult;
  }
}
