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

import javax.annotation.Nullable;

/** Responsible for creating and managing configurations. */
public interface ConfigurationManager {

  @Nullable ConfigurationFormat createDefaultWriter();

  /** Creates a configuration of the given class. On error, it throws an ConfigurationException. */
  <T extends Configuration> T createConfiguration(Class<T> type,
      ConfigurationFormat configurationWriter) throws ConfigurationException;

  /** Returns whether writing is supported for the given configuration. */
  default boolean supportsWrite(Configuration configuration) {
    return false;
  }

  /**
   * Writes the configuration. On error, it throws an ConfigurationException.
   *
   * <p>Note: when {@link #supportsWrite(Configuration)} returns false,
   * this method will throw an {@link UnsupportedOperationException}.
   */
  default void writeConfig(Configuration configuration, ConfigurationFormat configurationWriter)
      throws ConfigurationException {
    throw new UnsupportedOperationException("not supported yet");
  }
}
