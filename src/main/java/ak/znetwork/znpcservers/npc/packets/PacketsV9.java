package ak.znetwork.znpcservers.npc.packets;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCSlot;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;

import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.inventory.ItemStack;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * v1.9 - v1.16
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PacketsV9 extends PacketsV8 {
    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public PacketsV9(ZNPC npc) {
        super(npc);
    }

    @Override
    public void updateGlowPacket(Object packet) throws ReflectiveOperationException {
        final Object enumChatString = ClassTypes.ENUM_CHAT_TO_STRING_METHOD.invoke(getNPC().getGlowColor());
        ReflectionUtils.setValue(packet, "g", ClassTypes.GET_ENUM_CHAT_ID_METHOD.invoke(getNPC().getGlowColor()));
        if (Utils.BUKKIT_VERSION > 12) {
            ReflectionUtils.setValue(packet, "c", ClassTypes.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance(enumChatString));
        } else {
            ReflectionUtils.setValue(packet, "c", enumChatString);
        }
    }

    @Override
    public void updateEquipPacket(ZNPCSlot itemSlot, ItemStack item) throws ReflectiveOperationException {
        equipPackets.put(itemSlot.name(), ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD
                .newInstance(getNPC().getEntityID(), ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[itemSlot.getSlotNew()],
                        ClassTypes.AS_NMS_COPY_METHOD.invoke(ClassTypes.CRAFT_ITEM_STACK_CLASS, item)));
    }

    @Override
    public boolean allowGlowColor() {
        return true;
    }
}
