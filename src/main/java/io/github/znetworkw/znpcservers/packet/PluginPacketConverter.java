package io.github.znetworkw.znpcservers.packet;

import io.github.znetworkw.znpcservers.packet.internal.CachingPluginPacketConverter;

/**
 * Interface provides
 *
 * @param <T> the type that this converter provides.
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@FunctionalInterface
public interface PluginPacketConverter<T> {
    static <T> T of(T instance, Class<T> interfaceClass, PluginPacketCache cache) {
        return new CachingPluginPacketConverter<>(instance, cache).get(interfaceClass);
    }

    T get(Class<T> clazz);
}
