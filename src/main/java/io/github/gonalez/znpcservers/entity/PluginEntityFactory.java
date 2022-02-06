package io.github.gonalez.znpcservers.entity;

import io.github.gonalez.znpcservers.ZNPCs;
import io.github.gonalez.znpcservers.entity.internal.DefaultPluginEntityFactory;

import javax.annotation.Nullable;

/**
 * Factory interface for creating plugin entities.
 *
 * @param <T> the type of entity that this factory creates.
 */
@FunctionalInterface
public interface PluginEntityFactory<T extends PluginEntity> {
    static PluginEntityFactory<PerUserPluginEntity> of() {
        return new DefaultPluginEntityFactory(ZNPCs.SETTINGS.getUserStore(), ZNPCs.SETTINGS.getTaskManager());
    }

    /**
     * Creates a new plugin entity for the specified type and location.
     *
     * @param entityType the entity type.
     * @param spec the entity specifications.
     * @return a new plugin entity.
     */
    @Nullable
    T createPluginEntity(PluginEntityType entityType, PluginEntitySpec spec) throws Exception;
}
