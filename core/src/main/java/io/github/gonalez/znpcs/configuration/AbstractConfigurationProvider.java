package io.github.gonalez.znpcs.configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public abstract class AbstractConfigurationProvider implements ConfigurationProvider {

  private CacheLoader<Class<? extends Configuration>, ImmutableMap<String, Field>> configFields =
      new CacheLoader<Class<? extends Configuration>, ImmutableMap<String, Field>>() {
        @Override
        public ImmutableMap<String, Field> load(Class<? extends Configuration> configClass) {
          Map<String, Field> fields = new LinkedHashMap<>();
          for (Field field : configClass.getDeclaredFields()) {
            if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
              if (!field.isAccessible()) {
                field.setAccessible(true);
              }
              ConfigurationKey key = field.getAnnotation(ConfigurationKey.class);
              if (key != null) {
                fields.put(key.name(), field);
              }
            }
          }
          return ImmutableMap.copyOf(fields);
        }
      };

  final LoadingCache<Class<? extends Configuration>, ImmutableMap<String, Field>> cache =
      CacheBuilder.newBuilder().weakKeys().build(configFields);

  @Nullable
  @Override
  public <C extends Configuration> C getConfiguration(Class<C> configClass) throws IOException {
    C configuration;
    try {
      configuration = configClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IOException("Failed to create config: " + configClass, e);
    }
    ImmutableMap<String, Field> fields = cache.getUnchecked(configClass);
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

  abstract ImmutableMap<String, Object> readConfiguration(
      Class<? extends Configuration> configClass, ImmutableSet<String> keys) throws IOException;

  ImmutableMap<String, Field> getConfigFields(Class<? extends Configuration> configClass) {
    return cache.getUnchecked(configClass);
  }

  private ImmutableMap<String, Object> getConfigFieldValues(Configuration configuration) {
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    for (Map.Entry<String, Field> entry : cache.getUnchecked(configuration.getClass()).entrySet()) {
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
}
