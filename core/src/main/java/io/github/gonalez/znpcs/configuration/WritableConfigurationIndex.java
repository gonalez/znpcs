package io.github.gonalez.znpcs.configuration;

import java.io.IOException;

public interface WritableConfigurationIndex extends ConfigurationIndex {
  void writeConfiguration(Configuration configuration) throws IOException;
}
