package io.github.gonalez.znpcs.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractConfig implements ConfigFactory {
  static final LoadingCache<Class<? extends Config>, ImmutableMap<String, Field>> CONFIG_FIELD_CACHE =
      CacheBuilder.newBuilder()
          .weakKeys()
          .build(CacheLoader.from(AbstractConfig::accessConfigFields));

  private static ImmutableMap<String, Field> accessConfigFields(
      Class<? extends Config> configClass) {
    Map<String, Field> fields = new LinkedHashMap<>();
    for (Field field : configClass.getDeclaredFields()) {
      if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
        if (!field.isAccessible()) {
          field.setAccessible(true);
        }
        ConfigOption key = field.getAnnotation(ConfigOption.class);
        if (key != null) {
          fields.put(key.name(), field);
        }
      }
    }
    return ImmutableMap.copyOf(fields);
  }

  static ImmutableMap<String, Object> getConfigFieldValues(Config configuration) {
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, Field> entry : CONFIG_FIELD_CACHE.getUnchecked(configuration.getClass()).entrySet()) {
      Field field = entry.getValue();
      try {
        Object value = field.get(configuration);
        if (value != null) {
          builder.put(entry.getKey(), value);
        }
      } catch (IllegalAccessException e) {
        throw new IllegalStateException(e);
      }
    }
    return builder.build();
  }

  static ImmutableMap<String, Field> getConfigFields(Class<? extends Config> configClass) {
    return CONFIG_FIELD_CACHE.getUnchecked(configClass);
  }

  protected abstract ImmutableMap<String, Object> readConfiguration(
      Class<? extends Config> configClass, ImmutableSet<String> keys) throws IOException;

  @Override
  @Nullable
  public <C extends Config> C create(Class<C> configClass) throws IOException {
    C configuration;
    try {
      configuration = configClass.newInstance();
      configuration.configContext = () -> writeConfig(configuration);
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IOException("Failed to create config: " + configClass, e);
    }
    ImmutableMap<String, Field> fields = getConfigFields(configClass);
    ImmutableMap<String, Object> read = readConfiguration(configClass, fields.keySet());
    for (Map.Entry<String, Field> entry : fields.entrySet()) {
      Object test = read.get(entry.getKey());
      if (test != null) {
        try {
          entry.getValue().set(configuration, test);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return configuration;
  }

  protected void writeConfig(Config config) throws IOException {
    throw new UnsupportedOperationException("This config does not support writing");
  }
}
