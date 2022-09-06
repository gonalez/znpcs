package io.github.gonalez.znpcs.packet.internal;

import io.github.gonalez.znpcs.packet.PluginPacketCache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginPacketCache implements PluginPacketCache {
    private final Map<String, Object> cache = new HashMap<>();

    @Override
    public Object cacheResult(String key, Object result) {
        return cache.computeIfAbsent(key, (u) -> result);
    }

    @Override
    public void removeResult(String key) {
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(key));
    }

    @Override
    public Object getResult(String key) {
        return cache.get(key);
    }
}
