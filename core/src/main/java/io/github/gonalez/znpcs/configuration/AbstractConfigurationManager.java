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

import static com.google.common.collect.Maps.transformValues;

import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base class for implementations of {@link ConfigurationManager}.
 *
 * <p>Uses reflection to create configurations and populate their fields with
 * values given from {@link #readConfigValues}.
 */
public abstract class AbstractConfigurationManager implements ConfigurationManager {
  private static final ConcurrentHashMap<Class<? extends Configuration>,
        ImmutableMap<String, Field>> configFieldCache = new ConcurrentHashMap<>();

  static ImmutableMap<String, Field> getConfigFields(Class<? extends Configuration> type) {
    ImmutableMap<String, Field> configFieldInfos = configFieldCache.get(type);
    if (!configFieldCache.containsKey(type)) {
      ImmutableMap.Builder<String, Field> configFieldInfoBuilder = ImmutableMap.builder();
      for (Field field : type.getDeclaredFields()) {
        if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
          if (!field.isAccessible()) {
            field.setAccessible(true);
          }
          ConfigurationKey key = field.getAnnotation(ConfigurationKey.class);
          if (key != null) {
            configFieldInfoBuilder.put(key.name(), field);
          }
        }
      }
      configFieldInfos = configFieldInfoBuilder.build();
      configFieldCache.put(type, configFieldInfos);
    }
    return configFieldInfos;
  }

  static ImmutableMap<String, Class<?>> getConfigFieldsToFieldType(
      Class<? extends Configuration> configurationClass) {
    return ImmutableMap.copyOf(transformValues(getConfigFields(configurationClass), Field::getType));
  }

  public AbstractConfigurationManager() {}

  /**
   * Returns the entries to populate the fields of a configuration instance for
   * the given configuration class.
   *
   * <p>The returned map contains key-value pairs where the keys correspond to the names
   * of the configuration fields as defined by {@link ConfigurationKey#name()}, and the
   * values represent the desired values to be set for those fields.
   */
  protected abstract ImmutableMap<String, Object> readConfigValues(
      Class<? extends Configuration> config, ConfigurationFormat fieldResolver);

  @Override
  public <T extends Configuration> T createConfiguration(
      Class<T> type, ConfigurationFormat fieldResolver) {
    T configuration;
    try {
      configuration = type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new ConfigurationException("Failed to create config: " + type, e);
    }
    ImmutableMap<String, Field> configInfo = getConfigFields(type);
    if (!configInfo.isEmpty()) {
      ImmutableMap<String, Object> configValues = readConfigValues(type, fieldResolver);
      for (Entry<String, Field> entry : configInfo.entrySet()) {
        if (configValues.containsKey(entry.getKey())) {
          try {
            entry.getValue().set(configuration, configValues.get(entry.getKey()));
          } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                "Configuration fields should be accessible", e);
          }
        }
      }
    }
    return configuration;
  }
}
