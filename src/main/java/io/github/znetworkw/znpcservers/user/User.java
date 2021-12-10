package io.github.znetworkw.znpcservers.user;

import io.github.znetworkw.znpcservers.user.internal.ReflectivePluginUser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a basic, immutable user.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface User {
    /**
     * Creates a new, default user from the given player.
     *
     * @param player the player.
     * @return a new user.
     */
    static User of(Player player) throws Exception {
        return new ReflectivePluginUser(player);
    }

    /**
     * The unique user uuid.
     *
     * @return user uuid.
     */
    UUID getUUID();

    Location getLocation();

    void sendPackets(Object... packets) throws Exception;

    /**
     * Returns the online bukkit player for this user.
     *
     * @return
     *    the online player for this user, or
     *    {@code null} if no online player was found.
     */
    default Player getPlayer() {
        return Bukkit.getPlayer(getUUID());
    }
}
