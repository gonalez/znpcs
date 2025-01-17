package io.github.gonalez.znpcs.configuration;

import com.google.common.base.Preconditions;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.annotation.Nullable;


public class IndexConfigurationProvider implements ConfigurationProvider {
  private final ConfigurationIndex configurationIndex;
  private final ClassToInstanceMap<Configuration> configs = MutableClassToInstanceMap.create();

  public IndexConfigurationProvider(ConfigurationIndex configurationIndex) {
    this.configurationIndex = Preconditions.checkNotNull(configurationIndex);
  }

  public ConfigurationIndex getConfigurationIndex() {
    return configurationIndex;
  }

  @CanIgnoreReturnValue
  public <C extends Configuration> IndexConfigurationProvider setDefault(Class<C> type, C config) {
    configs.putInstance(type, config);
    return this;
  }

  @Nullable
  @Override
  public synchronized <C extends Configuration> C provideConfiguration(Class<C> configClass) {
    if (configs.containsKey(configClass)) {
      return configs.getInstance(configClass);
    }

    try {
      C createConfig = configurationIndex.createConfiguration(configClass);
      configs.putInstance(configClass, createConfig);
      return createConfig;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
