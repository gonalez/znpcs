package io.github.gonalez.znpcs.configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/** A cache for canonicalizing configuration values and associated fields. */
public final class ConfigurationCache {

  private static final LoadingCache<Class<? extends Configuration>, ImmutableMap<String, Field>>
      configurationFieldKeyNameCache =
          CacheBuilder.newBuilder()
              .build(new CacheLoader<>() {
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
              });

  static ImmutableMap<String, Field> getConfigFields(Class<? extends Configuration> clazz) {
    return configurationFieldKeyNameCache.getUnchecked(clazz);
  }

  public static ImmutableMap<String, Object> getConfigFieldValues(Configuration configuration) {
    ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
    for (Entry<String, Field> entry :
        configurationFieldKeyNameCache.getUnchecked(configuration.getClass()).entrySet()) {
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

  private ConfigurationCache() {}
}
