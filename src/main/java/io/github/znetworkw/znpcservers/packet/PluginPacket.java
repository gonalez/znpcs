package io.github.znetworkw.znpcservers.packet;

/**
 * A marker interface for packets.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginPacket {
    /**
     * A cached version of the {@link PluginPacket}, caching packet results.
     */
    interface Cached extends PluginPacket {
        PluginPacketCache getCache();
    }
}
