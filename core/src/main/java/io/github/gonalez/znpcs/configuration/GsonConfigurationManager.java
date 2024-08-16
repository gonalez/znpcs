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

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.gonalez.znpcs.configuration.ConfigurationFormat.Reader;
import io.github.gonalez.znpcs.configuration.ConfigurationFormat.Writer;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map.Entry;
import javax.annotation.Nullable;

/**
 * A {@link PathConfigurationManager} that reads and writes configurations from their
 * {@link #getPath path} in JSON format using Gson.
 */
public abstract class GsonConfigurationManager extends PathConfigurationManager {
  private final Gson gson;

  public GsonConfigurationManager(Gson gson) {
    this.gson = Preconditions.checkNotNull(gson);
  }

  @Nullable
  @Override
  public ConfigurationFormat createDefaultWriter() {
    return new GsonObjectConfigurationWriter(gson);
  }

  @Override
  public boolean supportsWrite(Configuration configuration) {
    return getPath(configuration.getClass()) != null;
  }

  @Override
  protected ImmutableMap<String, Object> readConfigValues(
      Class<? extends Configuration> configurationClass, ConfigurationFormat configurationFormat) {
    Path path = getPathOrThrow(configurationClass);
    ImmutableMap<String, Class<?>> fields = getConfigFieldsToFieldType(configurationClass);
    try (InputStream inputStream = Files.newInputStream(path);
        Reader reader = configurationFormat.open(inputStream)) {
      ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
      for (Entry<String, Class<?>> entry : fields.entrySet()) {
        builder.put(entry.getKey(), reader.readFromStream(entry.getKey(), entry.getValue()));
      }
      return builder.build();
    } catch (IOException e) {
      throw new ConfigurationException(String.format(
          "Failed to parse json from: %s, for %s", path, configurationClass), e);
    }
  }

  @Override
  public void writeConfig(Configuration configuration, ConfigurationFormat configurationFormat) {
    Path path = getPathOrThrow(configuration.getClass());
    ImmutableMap<String, Field> fields = getConfigFields(configuration.getClass());
    try (OutputStream outputStream = Files.newOutputStream(path);
        Writer writer = configurationFormat.open(outputStream)) {
      for (Entry<String, Field> entry : fields.entrySet()) {
        Object value = entry.getValue().get(configuration);
        writer.addForWrite(entry.getKey(), value);
      }
      writer.writeToStream(configuration);
    } catch (Exception e) {
      throw new ConfigurationException("Failed to write configuration: " + configuration, e);
    }
  }

  public static class GsonObjectConfigurationWriter implements ConfigurationFormat {
    private final Gson gson;
    private final JsonObject jsonObject;

    public GsonObjectConfigurationWriter(Gson gson) {
      this(gson, new JsonObject());
    }

    public GsonObjectConfigurationWriter(Gson gson, JsonObject jsonObject) {
      this.gson = Preconditions.checkNotNull(gson);
      this.jsonObject = Preconditions.checkNotNull(jsonObject);
    }

    GsonObjectConfigurationWriter getInstance() {
      return this;
    }

    @Override
    public Writer open(OutputStream outputStream) throws IOException {
      OutputStreamWriter out = new OutputStreamWriter(outputStream, UTF_8);
      return new Writer() {
        @Override
        public void addForWrite(String key, Object value) throws IOException {
          jsonObject.add(key, gson.toJsonTree(value));
        }

        @Override
        public void writeToStream(Configuration configuration) throws IOException {
          gson.toJson(jsonObject, out);
        }

        @Override
        public Writer open(OutputStream outputStream) throws IOException {
          return getInstance().open(outputStream);
        }

        @Override
        public Reader open(InputStream inputStream) throws IOException {
          return getInstance().open(inputStream);
        }

        @Override
        public void close() throws IOException {
          out.close();
        }
      };
    }

    @Override
    public Reader open(InputStream inputStream) throws IOException {
      InputStreamReader input = new InputStreamReader(inputStream, UTF_8);
      return new Reader() {

        @Override
        public <T> T readFromStream(String key, Class<T> type) throws IOException {
          JsonObject object = gson.fromJson(input, JsonObject.class);
          return gson.fromJson(object.get(key), type);
        }

        @Override
        public Writer open(OutputStream outputStream) throws IOException {
          return getInstance().open(outputStream);
        }

        @Override
        public Reader open(InputStream inputStream) throws IOException {
          return getInstance().open(inputStream);
        }

        @Override
        public void close() throws IOException {
          input.close();
        }
      };
    }
  }
}
