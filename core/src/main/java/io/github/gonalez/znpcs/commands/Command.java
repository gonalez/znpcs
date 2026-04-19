package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.context.Context;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Command {

  public abstract String getName();

  protected abstract CommandResult execute(
      CommandEnvironment env, Context context, ImmutableList<String> args);

  protected abstract int getMandatoryArguments();

  protected Collection<Command> getChildren() {
    return ImmutableList.of();
  }

  protected boolean executeOnChildrenFound() {
    return true;
  }

  public CommandResult executeCommand(
      CommandEnvironment env, Context context, ImmutableList<String> args) {
    Context mergedContext = context.mergeWith(env.getDefaultContext());
    return executeCommandRecursive(env, mergedContext, args);
  }

  private CommandResult executeCommandRecursive(
      CommandEnvironment env, Context context, ImmutableList<String> args) {
    CommandResult validateCommandResult = validateCommand(context, args);
    if (validateCommandResult.hasError()) {
      return validateCommandResult;
    }
    List<Command> possibleCommands = new ArrayList<>();
    for (int i = 0; i < args.size(); i++) {
      Iterable<Command> commands =
          (i == 0)
              ? getChildren()
              : Iterables.concat(Iterables.transform(possibleCommands, Command::getChildren));
      for (Command command : ImmutableList.copyOf(commands)) {
        if (command.getName().startsWith(args.get(i))) {
          possibleCommands.add(command);
        }
      }
    }
    if (!possibleCommands.isEmpty()) {
      if (executeOnChildrenFound()) {
        context = run(env, context, args).getContext();
      }
      return Iterables.getLast(possibleCommands).executeCommandRecursive(env, context, args);
    }
    return run(env, context, args);
  }

  private CommandResult run(CommandEnvironment env, Context context, ImmutableList<String> args) {
    CommandResult result = execute(env, context, args);
    result.setContext(context.mergeWith(result.getContext()));
    return result;
  }

  CommandResult validateCommand(Context context, ImmutableList<String> args) {
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

  protected CommandResult newCommandResult() {
    return CommandResult.create(this);
  }
}
