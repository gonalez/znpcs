package io.github.gonalez.znpcs.config;

import java.io.IOException;

public interface ConfigContext {
  void tryWriteConfig() throws IOException;
}
