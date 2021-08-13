package ak.znetwork.znpcservers.npc.packet;

import ak.znetwork.znpcservers.npc.ItemSlot;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.cache.CacheRegistry;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @inheritDoc
 */
public class PacketsV16 extends PacketsV9 {
    /**
     * Creates the packets for the given npc.
     *
     * @param npc The npc.
     */
    public PacketsV16(NPC npc) {
        super(npc);
    }

    @Override
    public void updateEquipPacket(ItemSlot itemSlot, ItemStack item) throws ReflectiveOperationException {
        List<Pair<?, ?>> pairs = Lists.newArrayListWithCapacity(ItemSlot.values().length);
        for (Map.Entry<ItemSlot, ItemStack> entry : getNPC().getNpcPojo().getNpcEquip().entrySet()) {
            pairs.add(new Pair<>(CacheRegistry.ENUM_ITEM_SLOT.getEnumConstants()[entry.getKey().getSlotNew()], CacheRegistry.AS_NMS_COPY_METHOD.invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, entry.getValue())));
        }
        equipPackets.put("ALL", CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_V1.newInstance(getNPC().getEntityID(), pairs));
    }
}
