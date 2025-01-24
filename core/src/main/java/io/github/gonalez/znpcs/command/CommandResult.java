package io.github.gonalez.znpcs.command;

/** Contains information about the result of {@link Command#execute}. This class is mutable. */
public final class CommandResult {
  public static CommandResult create(Command command) {
    return new CommandResult(command);
  }

  private CommandException error = null;
  private String errorMessage;

  private String successMessage;

  private final Command actualCommand;

  private CommandResult(Command actualCommand) {
    this.actualCommand = actualCommand;
  }

  public boolean hasError() {
    return errorMessage != null || error != null;
  }

  public CommandResult setError(CommandException error) {
    this.error = error;
    return this;
  }

  public CommandException getError() {
    return error;
  }

  public CommandResult setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public CommandResult setSuccessMessage(String successMessage) {
    this.successMessage = successMessage;
    return this;
  }

  public String getSuccessMessage() {
    return successMessage;
  }

  public Command getActualCommand() {
    return actualCommand;
  }
}
