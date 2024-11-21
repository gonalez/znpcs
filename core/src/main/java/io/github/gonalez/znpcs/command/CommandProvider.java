package io.github.gonalez.znpcs.command;

import javax.annotation.Nullable;

@FunctionalInterface
public interface CommandProvider {
  CommandProvider NOOP = commandClass -> null;

  @Nullable Command provideCommand(Class<? extends Command> commandClass);
}
