package io.github.gonalez.znpcservers.entity;

import org.bukkit.entity.EntityType;

/**
 * Represents a immutable entity type, containing the required information for the type.
 */
public interface PluginEntityType {
    /**
     * The unique name of this entity.
     *
     * @return unique name of this entity.
     */
    String getName();

    PluginEntityTypeData getData();

    /**
     * The bukkit entity type representing this entity type.
     *
     * @return bukkit entity type representing this entity type.
     */
    EntityType getBukkitEntityType();

    boolean isAvailable();

    default boolean isPlayer() {
        return getBukkitEntityType() == EntityType.PLAYER;
    }
}
