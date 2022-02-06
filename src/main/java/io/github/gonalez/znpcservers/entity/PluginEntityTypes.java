package io.github.gonalez.znpcservers.entity;

import io.github.gonalez.znpcservers.entity.internal.DefaultPluginEntityTypeData;
import io.github.gonalez.znpcservers.cache.CacheRegistry;
import org.bukkit.entity.EntityType;

public enum PluginEntityTypes implements PluginEntityType {
    PLAYER(EntityType.PLAYER, CacheRegistry.ENTITY_PLAYER_CLASS, CacheRegistry.PLAYER_CONSTRUCTOR_OLD.getParameterTypes()),
    ARMOR_STAND(EntityType.ARMOR_STAND, CacheRegistry.ENTITY_ARMOR_STAND_CLASS),
    ZOMBIE(EntityType.ZOMBIE, CacheRegistry.ENTITY_ZOMBIE_CLASS);

    private final EntityType entityType;
    private final Class<?> entityClass;
    private final Class<?>[] expectedConstructorTypes;

    PluginEntityTypes(
        EntityType entityType, Class<?> entityClass) {
        this(entityType, entityClass, new Class[]{CacheRegistry.WORLD_CLASS});
    }

    PluginEntityTypes(
        EntityType entityType, Class<?> entityClass,
        Class<?>[] expectedConstructorTypes) {
        this.entityType = entityType;
        this.entityClass = entityClass;
        this.expectedConstructorTypes = expectedConstructorTypes;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public PluginEntityTypeData getData() {
        return new DefaultPluginEntityTypeData(this, entityClass, expectedConstructorTypes);
    }

    @Override
    public EntityType getBukkitEntityType() {
        return entityType;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
