package io.github.gonalez.znpcs.configuration;

import javax.annotation.Nullable;

@FunctionalInterface
public interface ConfigurationProvider {
  @Nullable <C extends Configuration> C provideConfiguration(Class<C> configClass);
}
