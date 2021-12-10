package io.github.znetworkw.znpcservers.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import io.github.znetworkw.znpcservers.entity.internal.packet.PluginEntityPacketsV8;
import io.github.znetworkw.znpcservers.packet.PluginPacketInfo;
import io.github.znetworkw.znpcservers.packet.PluginPacketCache;
import io.github.znetworkw.znpcservers.packet.PluginPacketConverter;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public interface PluginEntityPackets {
    /**
     * Returns a new, cached entity packet for the specified server version
     * and cache storage, if there is no default entity packet found for
     * the given packet, the method will throw an {@link IllegalStateException}
     * as described in the method.
     *
     * @param version the server version to locate the packet.
     * @param cache the cache for storing the packet results.
     * @return a new, cached entity packet instance.
     * @throws IllegalStateException if no packet was found for the specified version.
     */
    static PluginEntityPackets of(int version, PluginPacketCache cache) {
        final PluginEntityPackets pluginEntityPackets;
        if (version > 5) {
            pluginEntityPackets = new PluginEntityPacketsV8();
        } else {
            throw new IllegalStateException(("unable to locate a entity packet for version " + version));
        }
        return PluginPacketConverter.of(pluginEntityPackets, PluginEntityPackets.class, cache);
    }

    /**
     * Resolves the packet version using the current server version.
     *
     * @param cache the cache for storing the packet results.
     * @return a new, cached entity packet instance.
     * @see #of(int, PluginPacketCache)
     */
    static PluginEntityPackets of(PluginPacketCache cache) {
        return of(Utils.BUKKIT_VERSION, cache);
    }

    @PluginPacketInfo(name = "destroyPacket")
    Object getDestroyPacket(int entityId) throws ReflectiveOperationException;

    @PluginPacketInfo(name = "equipmentSlot")
    Object getEquipmentSlot(int slotId);

    @PluginPacketInfo(name = "equipmentPackets")
    ImmutableList<Object> getEquipmentPackets(int entityID, Map<PluginEntityEquipmentSlot, ItemStack> equipmentMap) throws ReflectiveOperationException;

    @PluginPacketInfo(name = "scoreboardPackets")
    ImmutableList<Object> getScoreboardPackets(UUID uuid, GameProfile gameProfile) throws ReflectiveOperationException;

    Object getItemStack(ItemStack itemStack) throws ReflectiveOperationException;
}
