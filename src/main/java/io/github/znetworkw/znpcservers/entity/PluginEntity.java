package io.github.znetworkw.znpcservers.entity;

import com.mojang.authlib.GameProfile;
import io.github.znetworkw.znpcservers.entity.attribute.PluginEntityAttributes;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Represents an server entity.
 * <p>
 * To instantiate a new entity, it is recommended via an entity factory using the
 * {@link PluginEntityFactory#createPluginEntity(PluginEntityType, Location)} method.
 *
 * @see PluginEntityFactory
 * @see PluginEntityFactory#createPluginEntity(PluginEntityType, Location)
 */
public interface PluginEntity {
    PluginEntityAttributes.Attribute<GameProfile> GAME_PROFILE_ATTRIBUTE = PluginEntityAttributes.Attribute.of(GameProfile.class, new GameProfile(UUID.randomUUID(), "Notch"));

    /**
     * The unique uuid of this entity.
     *
     * @return unique uuid of this entity.
     */
    UUID getUUID();

    /**
     * The id of this entity.
     *
     * @return id of this entity.
     */
    int getId();

    /**
     * The location where the entity is located in.
     *
     * @return entity location.
     */
    Location getLocation();

    /**
     * The entity type of this entity.
     *
     * @return type of this entity.
     */
    PluginEntityType getPluginEntityType();

    /**
     * The attributes that this entity contains.
     *
     * @return attributes that this entity contains.
     */
    PluginEntityAttributes getAttributes();

    /**
     * Returns {@code true} if the entity is alive.
     *
     * @return {@code true} if the entity is alive.
     */
    boolean isAlive();

    /**
     * Kills the entity, removing all resources created by this entity.
     */
    void kill();

    void setName(String name);
    void setInvisible(boolean visible);
    void setLocation(Location location);
    void teleportTo(Location location);
    void lookAt(Location location);
    void equip(PluginEntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack);
}
