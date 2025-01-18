package io.github.gonalez.znpcs.user;

import java.util.UUID;

public interface User {
  /** Returns the unique ID associated with the user. */
  UUID getId();

  void sendChatText(String text);

  void sendDefinedText(String text);
}
