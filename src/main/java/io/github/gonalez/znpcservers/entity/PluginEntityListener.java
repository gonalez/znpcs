package io.github.gonalez.znpcservers.entity;

import org.bukkit.Location;

/**
 * Listener interface for listening to plugin entities method calls.
 */
public interface PluginEntityListener {
    static PluginEntityListener noop() {
        return n -> {};
    }

    /**
     * Called when a location of an entity is changed.
     *
     * @param location the new entity location.
     */
    void onLocationChanged(Location location);
}
