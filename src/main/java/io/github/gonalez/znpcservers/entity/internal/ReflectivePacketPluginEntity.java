package io.github.gonalez.znpcservers.entity.internal;

import io.github.gonalez.znpcservers.entity.*;
import io.github.gonalez.znpcservers.entity.attribute.PluginEntityAttributes;
import io.github.gonalez.znpcservers.replacer.StringReplacer;
import io.github.gonalez.znpcservers.cache.CacheRegistry;
import io.github.gonalez.znpcservers.entity.*;
import io.github.gonalez.znpcservers.user.User;
import io.github.gonalez.znpcservers.user.UserUtils;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A per user plugin entity implementation that uses reflection.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ReflectivePacketPluginEntity implements PerUserPluginEntity {
    private final PluginEntityType pluginEntityType;
    private final Object transformedEntity;
    private final PluginEntityPackets entityPackets;
    private final Set<User> receivers;

    private final UUID uuid;
    private final int id;

    private Location location;

    private final Map<PluginEntityEquipmentSlot, ItemStack> equipmentMap = new HashMap<>();

    private boolean dead = false;

    private final PluginEntityListener listener;

    public ReflectivePacketPluginEntity(
        PluginEntityType pluginEntityType, Object transformedEntity,
        PluginEntityPackets entityPackets, Set<User> receivers, PluginEntityListener listener) throws Exception {
        if (!CacheRegistry.ENTITY_CLASS.isAssignableFrom(transformedEntity.getClass())) {
            throw new IllegalStateException(
                String.format("invalid class for entity, got: %s, expected: %s",
                    transformedEntity.getClass().getName(), CacheRegistry.ENTITY_CLASS.getName()));
        }
        this.pluginEntityType = pluginEntityType;
        this.transformedEntity = transformedEntity;
        this.entityPackets = entityPackets;
        this.receivers = receivers;
        this.listener = listener;
        try {
            uuid = (UUID) CacheRegistry.GET_UNIQUE_ID_METHOD.invoke(transformedEntity);
            id = ((Integer) CacheRegistry.GET_ENTITY_ID.invoke(transformedEntity));
        } catch (ReflectiveOperationException operationException) {
            throw new IllegalStateException(operationException);
        }
    }

    @Override
    public PluginEntityType getPluginEntityType() {
        return pluginEntityType;
    }

    @Override
    public PluginEntityAttributes getAttributes() {
        return null;
    }

    @Override
    public boolean isAlive() {
        return dead;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void kill() {
        dead = true;
    }

    @Override
    public void setName(String name) {
        try {
            CacheRegistry.SET_CUSTOM_NAME_VISIBLE_METHOD.invoke(transformedEntity, true);
            CacheRegistry.SET_CUSTOM_NAME_OLD_METHOD.invoke(transformedEntity, StringReplacer.of(name));
        } catch (ReflectiveOperationException operationException) {
            throw failure(operationException);
        }
    }

    @Override
    public void setInvisible(boolean visible) {
        try {
            CacheRegistry.SET_INVISIBLE_METHOD.invoke(transformedEntity, visible);
        } catch (ReflectiveOperationException operationException) {
            throw failure(operationException);
        }
    }

    @Override
    public void setLocation(Location location) {
        try {
            CacheRegistry.SET_LOCATION_METHOD.invoke(transformedEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            this.location = location;
            teleportTo(location);
        } catch (ReflectiveOperationException operationException) {
            throw failure(operationException);
        }
    }

    @Override
    public void teleportTo(Location location) {
        try {
            UserUtils.sendPackets(receivers, CacheRegistry.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(transformedEntity));
            //lookAt(location);
            listener.onLocationChanged(location);
        } catch (ReflectiveOperationException operationException) {
            throw failure(operationException);
        }
    }

    @Override
    public void lookAt(Location location) {
        final Location direction = location.equals(getLocation()) ? location : getLocation().setDirection(location.subtract(getLocation()).toVector());
        try {
            UserUtils.sendPackets(receivers,
                CacheRegistry.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.newInstance(getId(), (byte) (direction.getYaw() * 256.0F / 360.0F), (byte) (direction.getPitch() * 256.0F / 360.0F), true),
                CacheRegistry.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.newInstance(transformedEntity, (byte) (direction.getYaw() * 256.0F / 360.0F)));
        } catch (ReflectiveOperationException operationException) {
            throw failure(operationException);
        }
    }

    @Override
    public void equip(PluginEntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        try {
            equipmentMap.put(entityEquipmentSlot, itemStack);
            UserUtils.sendPackets(receivers, entityPackets.getEquipmentPackets(id, equipmentMap).toArray());
        } catch (ReflectiveOperationException operationException) {
           throw failure(operationException);
        }
    }

    @Override
    public void onSpawn(User user) throws Exception {
        if (getPluginEntityType().isPlayer()) {
            user.sendPackets(CacheRegistry.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.newInstance(transformedEntity));
        } else {
            user.sendPackets(CacheRegistry.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(transformedEntity));
        }
        receivers.add(user);
    }

    @Override
    public void onDelete(User user) throws Exception {
        user.sendPackets(entityPackets.getDestroyPacket(id));
        receivers.remove(user);
    }

    @Override
    public boolean isViewer(User user) {
        return receivers.contains(user);
    }

    @Override
    public Iterable<User> getViewers() {
        return receivers;
    }

    /**
     * Creates an {@link UnexpectedCallException} for the specified exception.
     *
     * @param exception the exception.
     * @return a new exception.
     */
    static UnexpectedCallException failure(ReflectiveOperationException exception) {
        return new UnexpectedCallException(exception.getMessage(), exception);
    }
}
