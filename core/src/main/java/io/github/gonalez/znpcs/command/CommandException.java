package io.github.gonalez.znpcs.command;

import com.google.common.base.Preconditions;

public class CommandException extends Exception {
  private final Command command;

  public CommandException(String message, Command command) {
    super(message);
    this.command = Preconditions.checkNotNull(command);
  }

  public Command getCommand() {
    return command;
  }
}
