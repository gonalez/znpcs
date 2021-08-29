package io.github.znetworkw.znpcservers.npc.packet;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import io.github.znetworkw.znpcservers.npc.ItemSlot;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.Map;

public class PacketV8 implements Packet {

    @Override
    public int version() {
        return 8;
    }

    @Override
    public Object getPlayerPacket(Object nmsWorld, GameProfile gameProfile) throws ReflectiveOperationException {
        Constructor<?> constructor = Utils.BUKKIT_VERSION > 13 ? CacheRegistry.PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR : CacheRegistry.PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR;
        return CacheRegistry.PLAYER_CONSTRUCTOR_OLD.newInstance(
            CacheRegistry.GET_SERVER_METHOD.invoke(Bukkit.getServer()),
            nmsWorld,
            gameProfile,
            constructor.newInstance(nmsWorld));
    }

    @Override
    public Object getSpawnPacket(Object nmsEntity, boolean isPlayer) throws ReflectiveOperationException {
        return isPlayer ?
            CacheRegistry.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.newInstance(nmsEntity)
            : CacheRegistry.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(nmsEntity);
    }

    @Override
    public Object convertItemStack(int entityId, ItemSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException {
        return CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD.newInstance(
            entityId,
            itemSlot.getSlotOld(),
            CacheRegistry.AS_NMS_COPY_METHOD.invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, itemStack));
    }

    @Override
    public Object getClickType(Object interactPacket) throws ReflectiveOperationException {
        return Utils.getValue(interactPacket, "action");
    }

    @Override
    public ImmutableList<Object> getEquipPackets(NPC npc) throws ReflectiveOperationException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (Map.Entry<ItemSlot, ItemStack> stackEntry : npc.getNpcPojo().getNpcEquip().entrySet()) {
            builder.add(CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD.newInstance(
                npc.getEntityID(),
                stackEntry.getKey().getSlotOld(),
                convertItemStack(npc.getEntityID(), stackEntry.getKey(), stackEntry.getValue())));
        }
        return builder.build();
    }

    @Override
    public void updateGlowPacket(NPC npc, Object packet) throws ReflectiveOperationException {
        throw new IllegalStateException("Glow color is not supported for 1.8 version.");
    }

    @Override
    public boolean allowGlowColor() {
        return false;
    }
}
