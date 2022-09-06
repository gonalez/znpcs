package io.github.gonalez.znpcs.entity.internal.attribute;

import io.github.gonalez.znpcs.entity.attribute.PluginEntityAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginEntityAttributes implements PluginEntityAttributes {
    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public <T> T get(Attribute<T> attribute) {
        return attribute.getType().cast(store.get(attribute.getName()));
    }

    @Override
    public <T> T set(Attribute<T> attribute, T value) {
        return attribute.getType().cast(store.put(attribute.getName(), value));
    }
}
