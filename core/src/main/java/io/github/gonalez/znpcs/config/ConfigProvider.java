package io.github.gonalez.znpcs.config;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ConfigProvider {
  public static final ConfigProvider EMPTY =
      new ConfigProvider() {
        @Override
        public @Nullable <C extends Config> C getConfig(Class<C> configClass) {
          return null;
        }
      };

  @Nullable
  <C extends Config> C getConfig(Class<C> configClass);
}
