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

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.io.TempDir;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link Configuration}. */
@RunWith(JUnit4.class)
public class ConfigurationTest {
  @TempDir public Path tempFolder;

  public static final class ExampleConfig extends Configuration {

    @ConfigurationKey(name = "view_distance")
    public int viewDistance;

    @ConfigurationKey(name = "names")
    public List<String> names;
  }

  @Test
  public void testConfigurationManager_withGson_writeExampleConfig() throws Exception {
    Path testPath = tempFolder.resolve("config.json");

    GsonConfigurationIndex configurationManager =
        new GsonConfigurationIndex(new GsonBuilder().setPrettyPrinting().create()) {
          @Override
          public Path getConfigFilePath(Class<? extends Configuration> configClass) {
            return testPath;
          }
        };

    ExampleConfig exampleConfig = new ExampleConfig();
    exampleConfig.viewDistance = 32;

    configurationManager.writeConfiguration(exampleConfig);
    assertThat(Files.asCharSource(testPath.toFile(), UTF_8).read())
        .isEqualTo(
            "{\n"
                + "  \"view_distance\": 32\n"
                + "}");


    exampleConfig = configurationManager.createConfiguration(ExampleConfig.class);
    assertThat(exampleConfig.viewDistance).isEqualTo(32);
    assertThat(exampleConfig.names).isNull();
  }

  @Test
  public void testConfigurationManager_withYaml_writeExampleConfig() throws Exception {
    Path testPath = tempFolder.resolve("config.yml");

    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(FlowStyle.BLOCK);
    options.setIndent(2);

    YamlConfigurationIndex configurationManager =
        new YamlConfigurationIndex(new Yaml(options)) {
          @Override
          public Path getConfigFilePath(Class<? extends Configuration> configClass) {
            return testPath;
          };
        };

    ExampleConfig exampleConfig = new ExampleConfig();
    exampleConfig.viewDistance = 32;
    List<String> contents = Lists.newArrayList("hey", "world");
    exampleConfig.names = contents;

    configurationManager.writeConfiguration(exampleConfig);
    assertThat(Files.asCharSource(testPath.toFile(), UTF_8).read())
        .isEqualTo(
            "view_distance: 32\n"
            + "names:\n"
            + "- hey\n"
            + "- world\n");

    exampleConfig = configurationManager.createConfiguration(ExampleConfig.class);
    assertThat(exampleConfig.viewDistance).isEqualTo(32);
    assertThat(exampleConfig.names).isEqualTo(contents);
  }
}
