package io.github.gonalez.znpcs.command;

import com.google.common.collect.MutableClassToInstanceMap;
import javax.annotation.Nullable;

/** Contains information about the result of {@link Command#execute}. This class is mutable. */
public final class CommandResult {
  public static CommandResult create(Command command) {
    return new CommandResult(command);
  }

  private final Command actualCommand;

  private Throwable error = null;
  private String errorMessage;
  private String successMessage;

  final MutableClassToInstanceMap<Object> dependencies = MutableClassToInstanceMap.create();

  private CommandResult(Command actualCommand) {
    this.actualCommand = actualCommand;
  }

  public boolean hasError() {
    return errorMessage != null || error != null;
  }

  public <T> CommandResult addDependency(Class<T> type, T inst) {
    dependencies.put(type, inst);
    return this;
  }

  @Nullable
  public <T> T getDependency(Class<T> type) {
    return dependencies.getInstance(type);
  }

  public CommandResult setError(Throwable error) {
    this.error = error;
    return this;
  }

  public Throwable getError() {
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
