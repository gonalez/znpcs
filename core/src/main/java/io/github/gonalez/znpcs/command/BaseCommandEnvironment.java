package io.github.gonalez.znpcs.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import io.github.gonalez.znpcs.configuration.ConfigurationProvider;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class BaseCommandEnvironment implements CommandEnvironment {
  private final ImmutableClassToInstanceMap<Command> commands;
  private final ConfigurationProvider configurationProvider;

  public BaseCommandEnvironment(
      ImmutableClassToInstanceMap<Command> commands,
      ConfigurationProvider configurationProvider) {
    this.commands = checkNotNull(commands);
    this.configurationProvider = checkNotNull(configurationProvider);
  }

  @Override
  public CommandResult executeCommand(Command command, ImmutableList<String> args) {
    CommandResult validateCommandResult = validateCommand(command, args);
    if (validateCommandResult.hasError()) {
      return validateCommandResult;
    }
    List<Command> possibleCommands = new ArrayList<>();
    for (int i = 0; i < args.size(); i++) {
      Iterable<Command> commands = (i == 0)
          ? command.getChildren()
          : Iterables.concat(Iterables.transform(possibleCommands, Command::getChildren));
      for (Command command1 : ImmutableList.copyOf(commands)) {
        if (command1.getName().startsWith(args.get(i))) {
          possibleCommands.add(command1);
        }
      }
    }
    command = possibleCommands.isEmpty() ? command : possibleCommands.get(possibleCommands.size() - 1);
    try {
      CommandHooks commandHooks = new CommandHooks(command, args, this);
      return command.execute(args, this, commandHooks);
    } catch (CommandException commandException) {
      validateCommandResult.setError(commandException);
      return validateCommandResult;
    }
  }

  private CommandResult validateCommand(Command command, ImmutableList<String> args) {
    CommandResult commandResult = CommandResult.create(command);
    if (args.size() < command.getMandatoryArguments()) {
      commandResult.setErrorMessage(
          String.format(
              "got '%s' arguments, expected at least '%s'",
              args.size(), command.getMandatoryArguments()));
    }
    return command.validateCommand(commandResult);
  }

  @Nullable
  @Override
  public Command getKnownCommand(Class<? extends Command> configClass) {
    return commands.getInstance(configClass);
  }

  @Override
  public ConfigurationProvider getConfigurationProvider() {
    return configurationProvider;
  }
}
