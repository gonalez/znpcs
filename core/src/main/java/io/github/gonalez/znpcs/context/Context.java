package io.github.gonalez.znpcs.context;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public final class Context {
  /** Key used to store and retrieve values in a {@code Context}. */
  public static final class Key<T> {
    final String identifier;

    private Key(String identifier) {
      this.identifier = checkNotNull(identifier);
    }

    public static <T> Key<T> of(String identifier) {
      return new Key<>(identifier);
    }

    @Override
    public String toString() {
      return identifier;
    }
  }

  public static final Context DEFAULT_INSTANCE = new Context();
  private final ImmutableMap<String, Object> map;

  private Context() {
    this(ImmutableMap.of());
  }

  Context(Builder builder) {
    this(ImmutableMap.copyOf(builder.map));
  }

  private Context(ImmutableMap<String, Object> map) {
    this.map = map;
  }

  public Context mergeWith(@Nullable Context other) {
    return other == null ? this : toBuilder().putAllInternal(other.map).build();
  }

  public <T> @Nullable T get(Class<T> clazz) {
    @SuppressWarnings("unchecked")
    T value = (T) map.get(clazz.getName());
    return value;
  }

  public <T> T get(Key<T> key) {
    return get(key, null);
  }

  public <T> T get(Key<T> key, T defaultValue) {
    @SuppressWarnings("unchecked")
    T v = (T) map.get(key.identifier);
    return v != null ? v : defaultValue;
  }

  public Builder toBuilder() {
    return builder().putAllInternal(map);
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Builder for {@link Context}. */
  public static final class Builder {
    private final Map<String, Object> map = new HashMap<>();

    private Builder() {}

    @CanIgnoreReturnValue
    public <T> Builder put(Class<T> clazz, T value) {
      map.put(clazz.getName(), value);
      return this;
    }

    @CanIgnoreReturnValue
    public <T> Builder put(Key<T> key, T value) {
      map.put(key.identifier, value);
      return this;
    }

    @CanIgnoreReturnValue
    Builder putAllInternal(Map<String, ?> values) {
      map.putAll(values);
      return this;
    }

    @CanIgnoreReturnValue
    public Builder putAll(Map<? extends Key<?>, ?> values) {
      for (Map.Entry<? extends Key<?>, ?> entry : values.entrySet()) {
        map.put(entry.getKey().identifier, entry.getValue());
      }
      return this;
    }

    public Context build() {
      return new Context(this);
    }
  }
}
