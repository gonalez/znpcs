package io.github.gonalez.znpcs.npc.function;

/**
 * A value object interface that is usually used for checking if the attributes of
 * an {@link NpcFunctionContext} are valid when creating a new {@link AbstractNpcFunction}.
 *
 * @param <T> the type of object that this value refers to.
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcFunctionValue<T> {
    static <T> NpcFunctionValue<T> of(Class<T> typeClass, String name) {
        return new DefaultValue<>(name, typeClass);
    }

    /**
     * An unique identifier for this value.
     *
     * @return an unique identifier for this value.
     */
    String name();

    /**
     * The type of object that this value refers to.
     *
     * @return the type of object that this value refers to.
     */
    Class<T> getType();

    /**
     * A default implementation of the {@link NpcFunctionValue}.
     */
    final class DefaultValue<T> implements NpcFunctionValue<T> {
        private final String name;
        private final Class<T> type;

        public DefaultValue(String name, Class<T> type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Class<T> getType() {
            return type;
        }
    }
}
