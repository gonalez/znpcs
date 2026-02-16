package io.github.gonalez.znpcs.config;

import com.google.common.collect.ImmutableClassToInstanceMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DefaultConfigProvider implements ConfigProvider {
  private final ImmutableClassToInstanceMap<Config> configs;

  private DefaultConfigProvider(ImmutableClassToInstanceMap<Config> configs) {
    this.configs = configs;
  }

  @Override
  public @Nullable <C extends Config> C getConfig(Class<C> configClass) {
    return configs.getInstance(configClass);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final ImmutableClassToInstanceMap.Builder<Config> builder =
        ImmutableClassToInstanceMap.builder();

    public <T extends Config> Builder addConfig(Class<T> type, T config) {
      builder.put(type, config);
      return this;
    }

    public ConfigProvider build() {
      return new DefaultConfigProvider(builder.build());
    }
  }
}
