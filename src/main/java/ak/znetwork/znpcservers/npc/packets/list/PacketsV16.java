package ak.znetwork.znpcservers.npc.packets.list;

import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.ItemSlot;
import ak.znetwork.znpcservers.types.ClassTypes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * v1.16 - v1.17
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PacketsV16 extends PacketsV13 {
    /**
     * Creates the packets for the given npc.
     *
     * @param znpc The npc.
     */
    public PacketsV16(ZNPC znpc) {
        super(znpc);
    }

    @Override
    public void getEquipPacket(ItemSlot itemSlot, ItemStack item) throws ReflectiveOperationException {
        List<Pair<?, ?>> pairs = Lists.newArrayListWithCapacity(ItemSlot.values().length);
        for (Map.Entry<ItemSlot, ItemStack> entry : getNPC().getNpcPojo().getNpcEquip().entrySet()) {
            pairs.add(new Pair<>(ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[entry.getKey().getSlotNew()],
                    ClassTypes.AS_NMS_COPY_METHOD.invoke(ClassTypes.CRAFT_ITEM_STACK_CLASS, entry.getValue())));
        }
        equipPackets.put("ALL", ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_V1.newInstance(getNPC().getEntityID(), pairs));
    }
}
