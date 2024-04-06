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

import java.nio.file.Path;
import javax.annotation.Nullable;

/** Common functionality for path based configurations shared by concrete classes. */
abstract class PathConfigurationManager extends AbstractConfigurationManager {

  /**
   * Returns the registered Path for the given configuration class, or throws an
   * ConfigurationException if no path exists.
   */
  protected Path getPathOrThrow(Class<? extends Configuration> configurationClass)
      throws ConfigurationException {
    Path path = getPath(configurationClass);
    if (path == null) {
      throw new ConfigurationException("Path not configured for: " + configurationClass);
    }
    return path;
  }

  /**
   * Sets the path for the given configuration class. Any existing path with the same given
   * class, will be replaced with the new path.
   */
  public abstract void setPath(Class<? extends Configuration> configurationClass, Path path);

  /** Gets the path for the given configuration class or {@code null} if not found. */
  @Nullable public abstract Path getPath(Class<? extends Configuration> configurationClass);
}
