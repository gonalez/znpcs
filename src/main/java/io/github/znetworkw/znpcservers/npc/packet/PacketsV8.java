package io.github.znetworkw.znpcservers.npc.packet;

import io.github.znetworkw.znpcservers.npc.ItemSlot;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;

/**
 * @inheritDoc
 */
public class PacketsV8 extends AbstractPacket {
    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public PacketsV8(NPC npc) {
        super(npc);
    }

    @Override
    public void updateSpawnPacket(Object nmsWorld) throws ReflectiveOperationException {
        Constructor<?> playerInteractManager = Utils.BUKKIT_VERSION > 13 ? CacheRegistry.PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR : CacheRegistry.PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR;
        playerSpawnPacket = CacheRegistry.PLAYER_CONSTRUCTOR_OLD
                .newInstance(CacheRegistry.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, getNPC().getGameProfile(), playerInteractManager.newInstance(nmsWorld));
    }

    @Override
    public void updateGlowPacket(Object packet) throws ReflectiveOperationException {
        throw new IllegalStateException("glow color not supported for version 1.8");
    }

    @Override
    public Object getClickType(Object interactPacket) throws ReflectiveOperationException {
        return Utils.getValue(interactPacket, "action");
    }

    @Override
    public void updateEquipPacket(ItemSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException {
        equipPackets.put(itemSlot.name(), CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD
                .newInstance(getNPC().getEntityID(), itemSlot.getSlotOld(), CacheRegistry.AS_NMS_COPY_METHOD
                .invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, itemStack)));
    }

    @Override
    public boolean allowGlowColor() {
        return false;
    }
}
