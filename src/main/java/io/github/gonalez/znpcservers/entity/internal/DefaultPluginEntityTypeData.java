package io.github.gonalez.znpcservers.entity.internal;

import io.github.gonalez.znpcservers.entity.PluginEntityType;
import io.github.gonalez.znpcservers.entity.PluginEntityTypeData;

import java.lang.reflect.Constructor;
import java.util.Arrays;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultPluginEntityTypeData implements PluginEntityTypeData {
    private final PluginEntityType entityType;
    private final Class<?> entityClass;
    private final Constructor<?> constructor;

    public DefaultPluginEntityTypeData(
        PluginEntityType entityType,
        Class<?> entityClass,
        Class<?>[] expectedTypes) {
        this.entityType = entityType;
        this.entityClass = entityClass;
        Constructor<?> find = null;
        for (Constructor<?> constructor : entityClass.getDeclaredConstructors()) {
            if (Arrays.equals(constructor.getParameterTypes(), expectedTypes)) {
                find = constructor;
            }
        }
        if (find == null) {
            throw new IllegalStateException("cannot find constructor for: " + entityClass.getName());
        }
        this.constructor = find;
    }

    @Override
    public PluginEntityType getEntityType() {
        return entityType;
    }

    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }

    @Override
    public Constructor<?> getEntityConstructor() {
        return constructor;
    }
}
