package io.github.gonalez.znpcs.entity.internal;

import io.github.gonalez.znpcs.entity.attribute.PluginEntityAttributes;
import io.github.gonalez.znpcs.entity.PerUserPluginEntity;
import io.github.gonalez.znpcs.entity.PluginEntityEquipmentSlot;
import io.github.gonalez.znpcs.entity.PluginEntityListener;
import io.github.gonalez.znpcs.entity.PluginEntityType;
import io.github.gonalez.znpcs.user.User;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DelegatePerUserPluginEntity implements PerUserPluginEntity {
    private final PerUserPluginEntity delegate;

    public DelegatePerUserPluginEntity(PerUserPluginEntity delegate, PluginEntityListener listener) {
        this.delegate = delegate    ;
    }

    @Override
    public void onSpawn(User user) throws Exception {
        delegate.onSpawn(user);
    }

    @Override
    public void onDelete(User user) throws Exception {
        delegate.onDelete(user);
    }

    @Override
    public boolean isViewer(User user) {
        return delegate.isViewer(user);
    }

    @Override
    public Iterable<User> getViewers() {
        return delegate.getViewers();
    }

    @Override
    public UUID getUUID() {
        return delegate.getUUID();
    }

    @Override
    public int getId() {
        return delegate.getId();
    }

    @Override
    public Location getLocation() {
        return delegate.getLocation();
    }

    @Override
    public PluginEntityType getPluginEntityType() {
        return delegate.getPluginEntityType();
    }

    @Override
    public PluginEntityAttributes getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public boolean isAlive() {
        return delegate.isAlive();
    }

    @Override
    public void kill() {
        delegate.kill();
    }

    @Override
    public void setName(String name) {
        delegate.setName(name);
    }

    @Override
    public void setInvisible(boolean visible) {
        delegate.setInvisible(visible);
    }

    @Override
    public void setLocation(Location location) {
        delegate.setLocation(location);
    }

    @Override
    public void teleportTo(Location location) {
        delegate.teleportTo(location);
    }

    @Override
    public void lookAt(Location location) {
        delegate.lookAt(location);
    }

    @Override
    public void equip(PluginEntityEquipmentSlot entityEquipmentSlot, ItemStack itemStack) {
        delegate.equip(entityEquipmentSlot, itemStack);
    }
}
