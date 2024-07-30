package io.github.gonalez.znpcs.configuration;

import java.io.Writer;
import java.lang.reflect.Field;

public interface ConfigurationFieldResolver {
  void writeField(Writer writer, KeyAndValue keyAndValue);
  void writeConfig(Writer writer, Configuration configuration);
}
