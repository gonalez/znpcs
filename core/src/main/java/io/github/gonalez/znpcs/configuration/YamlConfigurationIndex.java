package io.github.gonalez.znpcs.configuration;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/** A {@link PathConfigurationIndex} that reads and writes configurations from YAML file. */
public abstract class YamlConfigurationIndex extends PathConfigurationIndex {
  private final Yaml yaml;

  public YamlConfigurationIndex(Yaml yaml) {
    this.yaml = checkNotNull(yaml);
  }

  @Override
  protected ImmutableMap<String, Object> readConfiguration(
      Reader reader, Class<? extends Configuration> configClass) {
    Map<String, Object> data = yaml.load(reader);
    return ImmutableMap.copyOf(data);
  }

  @Override
  protected void writeConfiguration(Writer writer, ImmutableMap<String, Object> values) {
    yaml.dump(values, writer);
  }
}
