package io.github.znetworkw.znpcservers.entity.attribute;

import io.github.znetworkw.znpcservers.entity.internal.attribute.DefaultAttribute;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginEntityAttributes {
    interface Attribute<T> {
        static <T> Attribute<T> of(String name, Class<T> type, T value) {
            return new DefaultAttribute<>(name, type, value);
        }

        static <T> Attribute<T> of(Class<T> type, T value) {
            return of(type.getName(), type, value);
        }

        String getName();

        Class<T> getType();
        T value();
    }

    <T> T get(Attribute<T> attribute);
    <T> T set(Attribute<T> attribute, T value);

    default <T> T get(Attribute<T> attribute, T defaultValue) {
        T maybeGet = get(attribute);
        if (maybeGet == null) {
            return set(attribute, defaultValue);
        }
        return maybeGet;
    }
}
