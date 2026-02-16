package io.github.gonalez.znpcs.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public abstract class Command {

  public abstract String getName();

  protected abstract CommandResult execute(
      CommandEnvironment env, CommandContext ctx, ImmutableList<String> args);

  protected abstract int getMandatoryArguments();

  protected Collection<Command> getChildren() {
    return ImmutableList.of();
  }

  protected boolean executeOnChildrenFound() {
    return true;
  }

  public CommandResult executeCommand(
      CommandEnvironment env, CommandContext ctx, ImmutableList<String> args) {
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
    if (!possibleCommands.isEmpty()) {
      if (executeOnChildrenFound()) {
        CommandResult result = execute(env, ctx, args);
        env.executedCommands.put(this, result);
        if (result.hasError()) {
          return result;
        }
        env.mergedCommandResult = mergeCommandResultDeps(env.mergedCommandResult, result);
      }
      return Iterables.getLast(possibleCommands).executeCommand(env, ctx, args);
    }
    return execute(env, ctx, args);
  }

  private static CommandResult mergeCommandResultDeps(
      @Nullable CommandResult saved, CommandResult result) {
    if (saved != null) {
      result.dependencies.putAll(saved.dependencies);
    }
    return result;
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
