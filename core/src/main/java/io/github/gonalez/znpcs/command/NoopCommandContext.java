package io.github.gonalez.znpcs.command;

public class NoopCommandContext implements CommandContext {
  public static final CommandContext INSTANCE = new NoopCommandContext();

  @Override
  public String getId() {
    return "";
  }

  @Override
  public void log(String message, Object... args) {

  }
}
