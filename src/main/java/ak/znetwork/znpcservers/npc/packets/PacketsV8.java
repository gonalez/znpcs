package ak.znetwork.znpcservers.npc.packets;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCSlot;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * v1.8 - v1.9
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PacketsV8 extends Packets {
    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public PacketsV8(ZNPC npc) {
        super(npc);
    }

    @Override
    public void updateSpawnPacket(Object nmsWorld) throws ReflectiveOperationException {
        Constructor<?> playerInteractManager = Utils.BUKKIT_VERSION > 13 ? ClassTypes.PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR : ClassTypes.PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR;
        playerSpawnPacket = ClassTypes.PLAYER_CONSTRUCTOR_OLD
                .newInstance(ClassTypes.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, getNPC().getGameProfile(), playerInteractManager.newInstance(nmsWorld));
    }

    @Override
    public void updateGlowPacket(Object packet) throws ReflectiveOperationException {
        throw new IllegalStateException("glow color not supported for version 1.8");
    }

    @Override
    public Object getDestroyPacket(int entityId) throws ReflectiveOperationException {
        return ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.getParameterTypes()[0].isArray() ? new int[]{entityId} : entityId);
    }

    @Override
    public Object getClickType(Object interactPacket) throws ReflectiveOperationException {
        return ReflectionUtils.getValue(interactPacket, "action");
    }

    @Override
    public void updateEquipPacket(ZNPCSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException {
        equipPackets.put(itemSlot.name(), ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD
                .newInstance(getNPC().getEntityID(), itemSlot.getSlotOld(), ClassTypes.AS_NMS_COPY_METHOD
                .invoke(ClassTypes.CRAFT_ITEM_STACK_CLASS, itemStack)));
    }

    @Override
    public boolean allowGlowColor() {
        return false;
    }
}
