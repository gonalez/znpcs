package io.github.gonalez.znpcs.config;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/** A {@link PathConfigFactory} that reads configurations from YAML file. */
public abstract class YamlConfigFactory extends PathConfigFactory {
  private final Yaml yaml;

  public YamlConfigFactory(Yaml yaml) {
    this.yaml = checkNotNull(yaml);
  }

  @Override
  protected ImmutableMap<String, Object> readConfiguration(
      Class<? extends Config> configClass, ImmutableSet<String> keys, Reader reader) {
    Map<String, Object> data = yaml.load(reader);
    return ImmutableMap.copyOf(data);
  }

  @Override
  protected void writeConfig(Config config, Writer writer) {
    yaml.dump(config.getFieldMap(), writer);
  }
}
