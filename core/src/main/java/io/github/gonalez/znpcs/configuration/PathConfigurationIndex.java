/*
 * Copyright 2024 - Gaston Gonzalez (Gonalez).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.gonalez.znpcs.configuration;

import com.google.common.collect.ImmutableMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public abstract class PathConfigurationIndex implements WritableConfigurationIndex {

  @Override
  public <T extends Configuration> T createConfiguration(Class<T> type) throws IOException {
    T configuration;
    try {
      configuration = type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IOException("Failed to create config: " + type, e);
    }
    Path configFilePath = getConfigFilePath(type);
    try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
      ImmutableMap<String, Object> values = readConfiguration(reader, type);
      for (Map.Entry<String, Field> entry : ConfigurationCache.getConfigFields(type).entrySet()) {
        Object test = values.get(entry.getKey());
        if (test != null) {
          try {
            entry.getValue().set(configuration, test);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
    return configuration;
  }

  @Override
  public void writeConfiguration(Configuration configuration) throws IOException {
    Path configFilePath = getConfigFilePath(configuration.getClass());
    try (BufferedWriter writer = Files.newBufferedWriter(configFilePath)) {
      writeConfiguration(writer, configuration.getFieldMap());
    }
  }

  protected abstract ImmutableMap<String, Object> readConfiguration(
      Reader reader, Class<? extends Configuration> configClass) throws IOException;

  protected abstract void writeConfiguration(
      Writer writer, ImmutableMap<String, Object> values) throws IOException;

  /**
   * Returns the path to the configuration file associated with the specified configuration class.
   *
   * @param configClass the class of the configuration for which the file path is to be retrieved.
   */
  public abstract Path getConfigFilePath(Class<? extends Configuration> configClass);
}
