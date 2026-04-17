package io.github.gonalez.znpcs.commands;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.github.gonalez.znpcs.commands.CommandContext.Builder;
import io.github.gonalez.znpcs.util.Translation;
import java.util.function.Consumer;

/** Contains information about the result of {@link Command#execute}. This class is mutable. */
public final class CommandResult {
  private final Command actualCommand;

  private Throwable error;
  private String errorMessage;
  private String successMessage;
  private CommandContext context;
  Consumer<CommandContext.Builder> contextPropagator;

  private CommandResult(Command actualCommand) {
    this.actualCommand = actualCommand;
  }

  CommandResult setContextPropagator(Consumer<CommandContext.Builder> contextPropagator) {
    this.contextPropagator = contextPropagator;
    return this;
  }

  public static CommandResult create(Command command) {
    return new CommandResult(command);
  }

  public Command getActualCommand() {
    return actualCommand;
  }

  public boolean hasError() {
    return errorMessage != null || error != null;
  }

  @CanIgnoreReturnValue
  public CommandResult setError(Throwable error) {
    this.error = error;
    return this;
  }

  public Throwable getError() {
    return error;
  }

  @CanIgnoreReturnValue
  CommandResult errorKey(String errorMessage) {
    this.errorMessage = Translation.get(errorMessage);
    return this;
  }

  @CanIgnoreReturnValue
  public CommandResult setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
    return this;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @CanIgnoreReturnValue
  CommandResult successKey(String successMessage) {
    this.successMessage = Translation.get(successMessage);
    return this;
  }

  @CanIgnoreReturnValue
  public CommandResult setSuccessMessage(String successMessage) {
    this.successMessage = successMessage;
    return this;
  }

  public String getSuccessMessage() {
    return successMessage;
  }

  @CanIgnoreReturnValue
  public CommandResult setContext(CommandContext context) {
    this.context = context;
    return this;
  }

  public CommandContext getContext() {
    return context;
  }
}
