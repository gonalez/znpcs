package io.github.znetworkw.znpcservers.entity.internal.packet;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.entity.PluginEntityEquipmentSlot;
import io.github.znetworkw.znpcservers.entity.PluginEntityPackets;
import io.github.znetworkw.znpcservers.packet.PluginPacketCache;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class PluginEntityPacketsV8 implements PluginEntityPackets {
    @Override
    public Object getDestroyPacket(int entityId) throws ReflectiveOperationException {
        boolean useArray = CacheRegistry.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.getParameterTypes()[0].isArray();
        return CacheRegistry.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(useArray ? new int[]{entityId} : entityId);
    }

    @Override
    public Object getEquipmentSlot(int slotId) {
        return CacheRegistry.ENUM_ITEM_SLOT.getEnumConstants()[slotId];
    }

    @Override
    public ImmutableList<Object> getEquipmentPackets(int entityID, Map<PluginEntityEquipmentSlot, ItemStack> equipmentMap) throws ReflectiveOperationException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (Map.Entry<PluginEntityEquipmentSlot, ItemStack> stackEntry : equipmentMap.entrySet()) {
            builder.add(CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD.newInstance(
                entityID,
                getEquipmentSlot(stackEntry.getKey().getSlot()),
                getItemStack(stackEntry.getValue())));
        }
        return builder.build();
    }

    @Override
    public ImmutableList<Object> getScoreboardPackets(UUID uuid, GameProfile gameProfile) throws ReflectiveOperationException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        final boolean isVersion17 = Utils.BUKKIT_VERSION > 16;
        final boolean isVersion9 = Utils.BUKKIT_VERSION > 8;
        Object scoreboardTeamPacket = isVersion17 ?
            CacheRegistry.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, gameProfile.getName()) : CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.newInstance();
        if (!isVersion17) {
            Utils.setValue(scoreboardTeamPacket, "a", gameProfile.getName());
            Utils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 1);
        }
        // add scoreboard delete packet
        builder.add(isVersion17 ? CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE_V1.invoke(null, scoreboardTeamPacket) : scoreboardTeamPacket);
        if (isVersion17) {
            // new class for scoreboard add packet
            scoreboardTeamPacket = CacheRegistry.SCOREBOARD_TEAM_CONSTRUCTOR.newInstance(null, gameProfile.getName());
            Utils.setValue(scoreboardTeamPacket, "e", gameProfile.getName());
            Utils.setValue(scoreboardTeamPacket, "l", CacheRegistry.ENUM_TAG_VISIBILITY_NEVER_FIELD);
        } else {
            // new class for scoreboard add packet
            scoreboardTeamPacket = CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR_OLD.newInstance();
            Utils.setValue(scoreboardTeamPacket, "a", gameProfile.getName());
            Utils.setValue(scoreboardTeamPacket, "e", "never");
            Utils.setValue(scoreboardTeamPacket, isVersion9 ? "i" : "h", 0);
        }
        // the entity list to update the scoreboard for (npc)
        Collection<String> collection = (Collection<String>) (isVersion17 ?
            CacheRegistry.SCOREBOARD_PLAYER_LIST.invoke(scoreboardTeamPacket) : Utils.getValue(scoreboardTeamPacket, isVersion9 ? "h" : "g"));
        if (gameProfile != null) {
            collection.add(gameProfile.getName());
        } else {
            // non-player entities must be added with their uuid
            collection.add(uuid.toString());
        }
        // check if packet version support glow color and the npc has glow activated
        builder.add(isVersion17 ? CacheRegistry.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CREATE.invoke(null, scoreboardTeamPacket, true) : scoreboardTeamPacket);
        return builder.build();
    }

    @Override
    public Object getItemStack(ItemStack itemStack) throws ReflectiveOperationException {
        return CacheRegistry.AS_NMS_COPY_METHOD.invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, itemStack);
    }
}
