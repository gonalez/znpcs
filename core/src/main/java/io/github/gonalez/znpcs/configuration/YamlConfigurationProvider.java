package io.github.gonalez.znpcs.configuration;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/** A {@link PathConfigurationProvider} that reads configurations from YAML file. */
public abstract class YamlConfigurationProvider extends PathConfigurationProvider {
  private final Yaml yaml;

  public YamlConfigurationProvider(Yaml yaml) {
    this.yaml = checkNotNull(yaml);
  }

  @Override
  ImmutableMap<String, Object> readConfiguration(
      Class<? extends Configuration> configClass, ImmutableSet<String> keys, Reader reader) {
    Map<String, Object> data = yaml.load(reader);
    return ImmutableMap.copyOf(data);
  }
}
