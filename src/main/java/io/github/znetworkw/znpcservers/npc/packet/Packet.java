package io.github.znetworkw.znpcservers.npc.packet;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import io.github.znetworkw.znpcservers.npc.*;

import io.github.znetworkw.znpcservers.cache.CacheRegistry;

import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;

/**
 * Contains a {@link NPC} packets for a bukkit version.
 */
public interface Packet {
    /**
     * Returns the version of this packet.
     *
     * @return The required bukkit version for this packet.
     */
    int version();

    /**
     * Creates the npc spawn packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    @PacketValue(keyName = "playerPacket")
    Object getPlayerPacket(Object nmsWorld, GameProfile gameProfile) throws ReflectiveOperationException;

    /**
     * Creates the npc spawn packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    @PacketValue(keyName = "spawnPacket")
    Object getSpawnPacket(Object entityNms, boolean isPlayer) throws ReflectiveOperationException;

    /**
     * Creates the item stack packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    Object convertItemStack(int entityId, ItemSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException;

    /**
     * Returns the click type for the given interact packet.
     *
     * @param interactPacket The interact packet.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    Object getClickType(Object interactPacket) throws ReflectiveOperationException;

    /**
     * Returns the meta data packet for the given nms entity.
     *
     * @param nmsEntity The nms entity.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    Object getMetadataPacket(int entityId, Object nmsEntity) throws ReflectiveOperationException;

    /**
     * Returns the spawn packet for the given hologram entity.
     *
     * @param armorStand The hologram entity.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    @PacketValue(keyName = "hologramSpawnPacket", valueType = ValueType.ARGUMENTS)
    Object getHologramSpawnPacket(Object armorStand) throws ReflectiveOperationException;

    /**
     * Returns the destroy packet for the given entity id.
     *
     * @param entityId The entity id.
     * @throws ReflectiveOperationException When failed to call the method.
     */
    @PacketValue(keyName = "destroyPacket", valueType = ValueType.ARGUMENTS)
    default Object getDestroyPacket(int entityId) throws ReflectiveOperationException {
        return CacheRegistry.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(CacheRegistry.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.getParameterTypes()[0].isArray() ? new int[]{entityId} : entityId);
    }

    /**
     * Returns the enum slot constant for the given slot.
     */
    @PacketValue(keyName = "enumSlot", valueType = ValueType.ARGUMENTS)
    default Object getItemSlot(int slot) {
        return CacheRegistry.ENUM_ITEM_SLOT.getEnumConstants()[slot];
    }

    /**
     * Creates the npc tab list remove packet.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    @PacketValue(keyName = "removeTab")
    default Object getTabRemovePacket(Object nmsEntity) throws ReflectiveOperationException {
        return CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(
            CacheRegistry.REMOVE_PLAYER_FIELD,
            Collections.singletonList(nmsEntity));
    }

    /**
     * Creates the npc equip packets.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    @PacketValue(keyName = "equipPackets")
    ImmutableList<Object> getEquipPackets(NPC npc) throws ReflectiveOperationException;

    /**
     * Updates the scoreboard packets for the npc.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    @PacketValue(keyName = "scoreboardPackets")
    default ImmutableList<Object> updateScoreboard(NPC npc) throws ReflectiveOperationException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        final boolean isVersion17 = Utils.BUKKIT_VERSION > 16;
        // +v1.9
        final boolean isVersion9 = Utils.BUKKIT_VERSION > 8;
        Object scoreboardTeamPacket = isVersion17 ?
            CacheRegistry.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, npc.getGameProfile().getName()) : CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.newInstance();
        if (!isVersion17) {
            Utils.setValue(scoreboardTeamPacket, "a", npc.getGameProfile().getName());
            Utils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 1);
        }
        // add scoreboard delete packet
        builder.add(isVersion17 ? CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1.invoke(null, scoreboardTeamPacket) : scoreboardTeamPacket);
        if (isVersion17) {
            // new class for scoreboard add packet
            scoreboardTeamPacket = CacheRegistry.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, npc.getGameProfile().getName());
            Utils.setValue(scoreboardTeamPacket, "e", npc.getGameProfile().getName());
            Utils.setValue(scoreboardTeamPacket, "l", CacheRegistry.ENUM_TAG_VISIBILITY_NEVER_FIELD);
        } else {
            // new class for scoreboard add packet
            scoreboardTeamPacket = CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.newInstance();
            Utils.setValue(scoreboardTeamPacket, "a", npc.getGameProfile().getName());
            Utils.setValue(scoreboardTeamPacket, "e", "never");
            Utils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 0);
        }
        // the entity list to update the scoreboard for (npc)
        Collection<String> collection = (Collection<String>) (isVersion17 ?
            CacheRegistry.SCOREBOARD_PLAYER_LIST.invoke(scoreboardTeamPacket) : Utils.getValue(scoreboardTeamPacket, isVersion9 ? "h" : "g"));
        if (npc.getNpcPojo().getNpcType() == NPCType.PLAYER) {
            collection.add(npc.getGameProfile().getName());
        } else {
            // non-player entities must be added with their uuid
            collection.add(npc.getUUID().toString());
        }
        // check if packet version support glow color and the npc has glow activated
        if (allowGlowColor() &&
        FunctionFactory.isTrue(npc, "glow")) {
            // update scoreboard with glow
            updateGlowPacket(npc, scoreboardTeamPacket);
        }
        builder.add(isVersion17 ? CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE.invoke(null, scoreboardTeamPacket, true) : scoreboardTeamPacket);
        return builder.build();
    }

    /**
     * Updates the scoreboard team with the npc glow color.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    void updateGlowPacket(NPC npc, Object packet) throws ReflectiveOperationException;

    /**
     * Determines if the version support glow color.
     *
     * @return {@code true} If the version support glow color.
     */
    boolean allowGlowColor();

    /**
     * Flushes the cache for default packets.
     *
     * @throws ReflectiveOperationException When failed to call the method.
     */
    default void update(PacketCache packetCache) throws ReflectiveOperationException {
        packetCache.flushCache("scoreboardPackets");
    }
}