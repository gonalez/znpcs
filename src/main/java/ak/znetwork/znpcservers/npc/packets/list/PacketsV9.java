package ak.znetwork.znpcservers.npc.packets.list;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCSlot;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;

import org.bukkit.inventory.ItemStack;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * v1.8 - 1.16
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PacketsV9 extends PacketsV8 {
    /**
     * Creates the packets for the given npc.
     *
     * @param znpc The npc.
     */
    public PacketsV9(ZNPC znpc) {
        super(znpc);
    }

    @Override
    public void updateGlowPacket(Object packetTeam) throws ReflectiveOperationException {
        ReflectionUtils.setValue(packetTeam, "g", ClassTypes.GET_ENUM_CHAT_ID_METHOD.invoke(getNPC().getGlowColor()));
        ReflectionUtils.setValue(packetTeam, "c", ClassTypes.ENUM_CHAT_TO_STRING_METHOD.invoke(getNPC().getGlowColor()));
    }

    @Override
    public void getEquipPacket(ZNPCSlot itemSlot, ItemStack item) throws ReflectiveOperationException {
        equipPackets.put(itemSlot.name(), ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD.newInstance(getNPC().getEntityID(), ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[itemSlot.getSlotNew()], ClassTypes.AS_NMS_COPY_METHOD.invoke(ClassTypes.CRAFT_ITEM_STACK_CLASS, item)));
    }
}
