package io.github.gonalez.znpcs.commands;

import com.google.common.collect.ImmutableClassToInstanceMap;
import javax.annotation.Nullable;

public final class CommandContext {
  public static final CommandContext DEFAULT_INSTANCE =
      new CommandContext(ImmutableClassToInstanceMap.of());
  private final ImmutableClassToInstanceMap<Object> dependencies;

  private CommandContext(ImmutableClassToInstanceMap<Object> dependencies) {
    this.dependencies = dependencies;
  }

  public <T> @Nullable T get(Class<T> type) {
    return dependencies.getInstance(type);
  }

  Builder toBuilder() {
    Builder builder = builder();
    builder.dependenciesBuilder.putAll(dependencies);
    return builder;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private final ImmutableClassToInstanceMap.Builder<Object> dependenciesBuilder
        = ImmutableClassToInstanceMap.builder();

    Builder() {}

    public <T> Builder put(Class<T> type, T value) {
      dependenciesBuilder.put(type, value);
      return this;
    }

    public CommandContext build() {
      return new CommandContext(dependenciesBuilder.build());
    }
  }
}
