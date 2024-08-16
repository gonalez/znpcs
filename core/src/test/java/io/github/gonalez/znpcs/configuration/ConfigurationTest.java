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

import static com.google.common.truth.Truth.assertThat;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.google.gson.Gson;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Unit tests for {@link Configuration}. */
@RunWith(JUnit4.class)
public class ConfigurationTest {
  private static final String EXAMPLE_CONFIG = "{\"view_distance\":32}";
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();

  public static final class ExampleConfig extends Configuration {

    @ConfigurationKey(name = "view_distance")
    public int viewDistance;
  }

  @Test
  public void testConfigurationManager_withGson_readExampleConfig() throws Exception {
    Path testPath = tempFolder.getRoot().toPath().resolve("testconfig.json");
    MoreFiles.asByteSink(testPath)
        .asCharSink(UTF_8)
        .write(EXAMPLE_CONFIG);

    ConfigurationManager configurationManager =
        new GsonConfigurationManager(new Gson()) {
          @Override
          public void setPath(Class<? extends Configuration> config, Path path) {
            throw new UnsupportedOperationException();
          }

          @Override
          public Path getPath(Class<? extends Configuration> config) {
            return testPath;
          }
        };

    ExampleConfig configuration = configurationManager.createConfiguration(
        ExampleConfig.class, configurationManager.createDefaultWriter());
    assertThat(configuration.viewDistance).isEqualTo(32);
  }

  @Test
  public void testConfigurationManager_withGson_writeExampleConfig() throws Exception {
    Path testPath = tempFolder.getRoot().toPath().resolve("testconfig.json");
    ConfigurationManager configurationManager =
        new GsonConfigurationManager(new Gson()) {
          @Override
          public void setPath(Class<? extends Configuration> config, Path path) {
            throw new UnsupportedOperationException();
          }

          @Override
          public Path getPath(Class<? extends Configuration> config) {
            return testPath;
          }
        };

    ExampleConfig configuration = new ExampleConfig();
    configuration.viewDistance = 32;

    configurationManager.writeConfig(configuration, configurationManager.createDefaultWriter());
    assertThat(Files.asCharSource(testPath.toFile(), UTF_8).read()).isEqualTo(EXAMPLE_CONFIG);
  }
}
