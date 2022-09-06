package io.github.gonalez.znpcs.entity;

import java.lang.reflect.Constructor;

/**
 * Data containing representations for an entity type.
 */
public interface PluginEntityTypeData {
    PluginEntityType getEntityType();

    Class<?> getEntityClass();
    Constructor<?> getEntityConstructor();
}
