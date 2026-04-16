package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        if (result.hasError()) {
          return result;
        }
        ctx = resolveContext(ctx, result);
      }
      return Iterables.getLast(possibleCommands).executeCommand(env, ctx, args);
    }
    return execute(env, ctx, args);
  }

  private CommandContext resolveContext(CommandContext context, CommandResult result) {
    CommandContext resultContext = result.getContext();
    if (resultContext != null) {
      return resultContext;
    }
    if (result.contextPropagator != null) {
      CommandContext.Builder builder = context.toBuilder();
      result.contextPropagator.accept(builder);
      return builder.build();
    }
    return context;
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
