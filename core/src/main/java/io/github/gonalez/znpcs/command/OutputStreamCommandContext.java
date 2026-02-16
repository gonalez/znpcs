package io.github.gonalez.znpcs.command;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamCommandContext implements CommandContext {
  private final String id;
  private final OutputStream outputStream;

  public OutputStreamCommandContext(String id, OutputStream outputStream) {
    this.id = id;
    this.outputStream = outputStream;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void log(String message, Object... args) {
    try {
      message = String.format(message, args);
      outputStream.write(message.getBytes(UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
