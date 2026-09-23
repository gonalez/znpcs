package io.github.gonalez.znpcs.metadata;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@AutoValue
@Immutable
public abstract class MetadataKey<T> {

  MetadataKey() {}

  public abstract @Nullable String getIdentifier();

  public abstract Type getType();

  public static ImmutableMap<String, MetadataKey<?>> toMap(Iterable<MetadataKey<?>> keys) {
    ImmutableMap.Builder<String, MetadataKey<?>> builder = ImmutableMap.builder();
    for (MetadataKey<?> key : keys) {
      if (key.getIdentifier() != null) {
        builder.put(key.getIdentifier(), key);
      }
    }
    return builder.build();
  }

  public static <T> MetadataKey<T> of(Type type) {
    return of(null, type);
  }

  public static <T> MetadataKey<T> of(String identifier, Type type) {
    return new AutoValue_MetadataKey<>(identifier, type);
  }
}
