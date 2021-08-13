package ak.znetwork.znpcservers.npc.packet;

import ak.znetwork.znpcservers.npc.ItemSlot;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.cache.CacheRegistry;
import ak.znetwork.znpcservers.utility.Utils;
import org.bukkit.inventory.ItemStack;

/**
 * @inheritDoc
 */
public class PacketsV9 extends PacketsV8 {
    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public PacketsV9(NPC npc) {
        super(npc);
    }

    @Override
    public void updateGlowPacket(Object packet) throws ReflectiveOperationException {
        final Object enumChatString = CacheRegistry.ENUM_CHAT_TO_STRING_METHOD.invoke(getNPC().getGlowColor());
        if (Utils.BUKKIT_VERSION > 12) {
            Utils.setValue(packet, "g", getNPC().getGlowColor());
            Utils.setValue(packet, "c", CacheRegistry.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance(enumChatString));
        } else {
            Utils.setValue(packet, "g", CacheRegistry.GET_ENUM_CHAT_ID_METHOD.invoke(getNPC().getGlowColor()));
            Utils.setValue(packet, "c", enumChatString);
        }
    }

    @Override
    public void updateEquipPacket(ItemSlot itemSlot, ItemStack item) throws ReflectiveOperationException {
        equipPackets.put(itemSlot.name(), CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD
                .newInstance(getNPC().getEntityID(), CacheRegistry.ENUM_ITEM_SLOT.getEnumConstants()[itemSlot.getSlotNew()],
                        CacheRegistry.AS_NMS_COPY_METHOD.invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, item)));
    }

    @Override
    public boolean allowGlowColor() {
        return true;
    }
}
