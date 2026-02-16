package io.github.gonalez.znpcs.command;

public interface CommandContext {
  String getId();

  void log(String message, Object... args);
}
