package io.github.gonalez.znpcs.packet;

import io.github.gonalez.znpcs.packet.internal.DefaultPluginPacketCache;

/**
 * A cache interface for storing and retrieval of packet results.
 */
public interface PluginPacketCache {
    /**
     * Creates a new, default plugin packet cache.
     *
     * @return a new plugin packet cache.
     */
    static PluginPacketCache of() {
        return new DefaultPluginPacketCache();
    }

    /**
     * Caches the specified value into the cache.
     *
     * @param key the key name in which this value will be stored.
     */
    Object cacheResult(String key, Object result);

    /**
     * Removes a cache value if there is any matching the specified key name.
     *
     * @param key the value matching a cache value.
     */
    void removeResult(String key);

    /**
     * Gets the value matching the specified key from the cache.
     *
     * @param key the key of the value.
     * @return
     *    the value matching the specified key, or {@code null}
     *    if no value was found matching the specified key.
     */
    Object getResult(String key);

    /**
     * Removes multiple cache values matching the specified key names.
     *
     * @param keys the key names.
     * @see #removeResult(String)
     */
    default void removeResult(String... keys) {
        for (String key : keys) {
            removeResult(key);
        }
    }
}
