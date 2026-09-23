package io.github.gonalez.znpcs.metadata;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class Metadata implements Iterable<MetadataKey<?>> {
  public static final Metadata DEFAULT_INSTANCE = Metadata.builder().build();

  private final Map<MetadataKey<?>, Object> values;

  Metadata(Builder builder) {
    this.values = builder.map;
  }

  public Metadata mergeWith(@Nullable Metadata other) {
    return other == null ? this : toBuilder().putAllInternal(other.values).build();
  }

  public Builder toBuilder() {
    return new Builder().putAllInternal(values);
  }

  public <T> @Nullable T get(Class<T> type) {
    return get(MetadataKey.of(type));
  }

  public <T> @Nullable T get(MetadataKey<T> key) {
    @SuppressWarnings("unchecked")
    T value = (T) values.get(key);
    return value;
  }

  public Iterable<MetadataKey<?>> identifiedKeys() {
    return filter(metadataKey -> metadataKey.getIdentifier() != null);
  }

  public Iterable<MetadataKey<?>> filter(Predicate<MetadataKey<?>> predicate) {
    return () -> values.keySet().stream().filter(predicate).iterator();
  }

  @Override
  public @Nonnull Iterator<MetadataKey<?>> iterator() {
    return values.keySet().iterator();
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Builder for {@link Metadata}. */
  public static final class Builder {
    private final Map<MetadataKey<?>, Object> map = new HashMap<>();

    Builder() {}

    @CanIgnoreReturnValue
    public <T> Builder put(MetadataKey<T> key, T value) {
      map.put(key, value);
      return this;
    }

    @CanIgnoreReturnValue
    public <T> Builder put(Class<T> type, T value) {
      map.put(MetadataKey.of(type), value);
      return this;
    }

    @CanIgnoreReturnValue
    private Builder putAllInternal(Map<MetadataKey<?>, Object> values) {
      map.putAll(values);
      return this;
    }

    public Metadata build() {
      return new Metadata(this);
    }
  }
}
