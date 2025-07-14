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
import com.google.common.collect.ImmutableSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class PathConfigurationProvider extends AbstractConfigurationProvider {

  abstract ImmutableMap<String, Object> readConfiguration(
      Class<? extends Configuration> configClass,
      ImmutableSet<String> keys, Reader reader) throws IOException;

  @Override
  final ImmutableMap<String, Object> readConfiguration(
      Class<? extends Configuration> configClass, ImmutableSet<String> keys) throws IOException {
    Path configFilePath = getConfigFilePath(configClass);
    try (BufferedReader reader = Files.newBufferedReader(configFilePath)) {
      return readConfiguration(configClass, keys, reader);
    }
  }

  /** Returns the path to the configuration file associated with the specified configuration class. */
  public abstract Path getConfigFilePath(Class<? extends Configuration> configClass);
}
