package io.github.gonalez.znpcs.entity.internal.attribute;

import io.github.gonalez.znpcs.entity.attribute.PluginEntityAttributes;

/**
 * @param <T>
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultAttribute<T> implements PluginEntityAttributes.Attribute<T> {
    private final String name;

    private final Class<T> type;
    private final T value;

    public DefaultAttribute(String name, Class<T> type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public T value() {
        return value;
    }
}
