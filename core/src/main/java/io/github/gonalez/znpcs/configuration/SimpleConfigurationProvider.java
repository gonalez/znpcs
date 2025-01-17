package io.github.gonalez.znpcs.configuration;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

public class SimpleConfigurationProvider implements ConfigurationProvider {
  public static Builder newBuilder() {
    return new Builder();
  }

  private final ImmutableClassToInstanceMap<Configuration> configs;

  private SimpleConfigurationProvider(ImmutableClassToInstanceMap<Configuration> configs) {
    this.configs = configs;
  }

  @Nullable
  @Override
  public <C extends Configuration> C provideConfiguration(Class<C> configClass) {
    return configs.getInstance(configClass);
  }

  /** Builder for {@link SimpleConfigurationProvider}. */
  public static final class Builder {
    private final ImmutableClassToInstanceMap.Builder<Configuration> configsBuilder =
        ImmutableClassToInstanceMap.builder();

    Builder() {}

    @CanIgnoreReturnValue
    public <C extends Configuration> Builder addConfiguration(Class<C> type, C config) {
      configsBuilder.put(type, config);
      return this;
    }

    @CanIgnoreReturnValue
    public <C extends Configuration> Builder addConfigurations(ClassToInstanceMap<C> configs) {
      configsBuilder.putAll(configs);
      return this;
    }

    // Creates a new SimpleConfigurationProvider from this builder.
    public ConfigurationProvider build() {
      return new SimpleConfigurationProvider(configsBuilder.build());
    }
  }
}
