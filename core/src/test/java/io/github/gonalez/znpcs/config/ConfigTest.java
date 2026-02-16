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

package io.github.gonalez.znpcs.config;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/** Unit tests for {@link Config}. */
@RunWith(Parameterized.class)
public class ConfigTest {
  @Rule public TemporaryFolder tempFolder = new TemporaryFolder();
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  private static final Yaml YAML = new Yaml(getYamlOptions());

  private static DumperOptions getYamlOptions() {
    DumperOptions opts = new DumperOptions();
    opts.setIndent(2);
    opts.setPrettyFlow(true);
    opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
    return opts;
  }

  @Parameterized.Parameters
  public static List<Object[]> data() {
    return Arrays.asList(
        new Object[][] {
            {
                "config.yml",
                "view_distance: 32\n" +
                    "names:\n" +
                    "- hello\n" +
                    "- world\n",
                (Function<Path, ConfigFactory>) path ->
                    new YamlConfigFactory(YAML) {
                      @Override
                      public Path getConfigFilePath(Class<? extends Config> configClass) {
                        return path;
                      }
                    }
            },
            {
                "config.json",
                "{\n" +
                    "  \"view_distance\": 32,\n" +
                    "  \"names\": [\n" +
                    "    \"hello\",\n" +
                    "    \"world\"\n" +
                    "  ]\n" +
                    "}",
                (Function<Path, ConfigFactory>) path ->
                    new GsonConfigFactory(GSON) {
                      @Override
                      public Path getConfigFilePath(Class<? extends Config> configClass) {
                        return path;
                      }
                    }
            }
        });
  }

  private final String fileName;
  private final String fileContent;
  private final Function<Path, ConfigFactory> factoryProvider;

  public ConfigTest(
      String fileName,
      String fileContent,
      Function<Path, ConfigFactory> factoryProvider) {
    this.fileName = fileName;
    this.fileContent = fileContent;
    this.factoryProvider = factoryProvider;
  }

  public static final class ExampleConfig extends Config {

    @ConfigOption(name = "view_distance")
    public int viewDistance;

    @ConfigOption(name = "names")
    public List<String> names;
  }

  @Test
  public void testFactoryConfig_create() throws Exception {
    Path path = tempFolder.newFile(fileName).toPath();
    Files.write(path, fileContent.getBytes(StandardCharsets.UTF_8));

    ConfigFactory factory = factoryProvider.apply(path);

    ExampleConfig exampleConfig = factory.create(ExampleConfig.class);
    assertThat(exampleConfig.viewDistance).isEqualTo(32);
    assertThat(exampleConfig.names).isEqualTo(Lists.newArrayList("hello", "world"));
  }

  @Test
  public void testFactoryConfig_write() throws Exception {
    Path path = tempFolder.newFile(fileName).toPath();
    Files.write(path, fileContent.getBytes(StandardCharsets.UTF_8));

    ConfigFactory factory = factoryProvider.apply(path);

    ExampleConfig exampleConfig = factory.create(ExampleConfig.class);

    assertThat(exampleConfig.viewDistance).isEqualTo(32);

    exampleConfig.viewDistance = 16;
    exampleConfig.getConfigContext().tryWriteConfig();

    exampleConfig = factory.create(ExampleConfig.class);

    assertThat(exampleConfig.viewDistance).isEqualTo(16);
  }

  @Test
  public void testConfigProvider_builder() {
    DefaultConfigProvider.Builder builder = DefaultConfigProvider.builder();
    ExampleConfig exampleConfig = new ExampleConfig();
    builder.addConfig(ExampleConfig.class, exampleConfig);
    ConfigProvider configProvider = builder.build();
    assertThat(configProvider.getConfig(ExampleConfig.class)).isEqualTo(exampleConfig);
  }
}
