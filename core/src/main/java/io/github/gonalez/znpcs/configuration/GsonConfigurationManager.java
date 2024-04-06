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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;

/**
 * A {@link PathConfigurationManager} that reads and writes configurations from their
 * {@link #getPath path} in JSON format using Gson.
 */
public abstract class GsonConfigurationManager extends PathConfigurationManager {
  private final Gson gson;

  public GsonConfigurationManager(Gson gson) {
    this.gson = Preconditions.checkNotNull(gson);
  }

  @Override
  public boolean supportWrite(Configuration configuration) {
    return getPath(configuration.getClass()) != null;
  }

  @Override
  protected ImmutableMap<String, Object> readConfigValues(
      Class<? extends Configuration> configurationClass) {
    Path path = getPathOrThrow(configurationClass);
    ImmutableMap<String, Class<?>> fields =
        getConfigFieldsToFieldType(configurationClass);

    try (Reader reader = Files.newBufferedReader(path)) {
      JsonObject object = gson.fromJson(reader, JsonObject.class);
      ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
      for (Entry<String, Class<?>> entry : fields.entrySet()) {
        builder.put(entry.getKey(), gson.fromJson(
            object.get(entry.getKey()), entry.getValue()));
      }
      return builder.build();
    } catch (IOException e) {
      throw new ConfigurationException(String.format(
          "Failed to parse json from: %s, for %s", path, configurationClass), e);
    }
  }

  @Override
  public void writeConfig(Configuration configuration) {
    Class<? extends Configuration> configurationClass = configuration.getClass();
    Path path = getPathOrThrow(configurationClass);
    ImmutableMap<String, Field> fields = getConfigFields(configuration.getClass());

    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      JsonObject jsonObject = new JsonObject();
      for (Entry<String, Field> entry : fields.entrySet()) {
        jsonObject.add(entry.getKey(), gson.toJsonTree(entry.getValue().get(configuration)));
      }
      gson.toJson(jsonObject, writer);
    } catch (Exception e) {
      throw new ConfigurationException("Failed to write configuration: " + configuration, e);
    }
  }
}
