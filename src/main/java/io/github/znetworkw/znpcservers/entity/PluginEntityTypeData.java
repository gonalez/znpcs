package io.github.znetworkw.znpcservers.entity;

import java.lang.reflect.Constructor;

/**
 * Data containing representations for an entity type.
 */
public interface PluginEntityTypeData {
    PluginEntityType getEntityType();

    Class<?> getEntityClass();
    Constructor<?> getEntityConstructor();
}
