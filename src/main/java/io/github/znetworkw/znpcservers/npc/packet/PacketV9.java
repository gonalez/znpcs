package io.github.znetworkw.znpcservers.npc.packet;

import com.google.common.collect.ImmutableList;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.npc.ItemSlot;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class PacketV9 extends PacketV8 {

    @Override
    public int version() {
        return 9;
    }

    @Override
    public Object convertItemStack(int entityId, ItemSlot itemSlot, ItemStack itemStack) throws ReflectiveOperationException {
        return CacheRegistry.AS_NMS_COPY_METHOD.invoke(CacheRegistry.CRAFT_ITEM_STACK_CLASS, itemStack);
    }

    @Override
    public ImmutableList<Object> getEquipPackets(NPC npc) throws ReflectiveOperationException {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        for (Map.Entry<ItemSlot, ItemStack> stackEntry : npc.getNpcPojo().getNpcEquip().entrySet()) {
            builder.add(CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD.newInstance(
                npc.getEntityID(),
                getItemSlot(stackEntry.getKey().getSlot()),
                convertItemStack(npc.getEntityID(), stackEntry.getKey(), stackEntry.getValue())));
        }
        return builder.build();
    }

    @Override
    public void updateGlowPacket(NPC npc, Object packet) throws ReflectiveOperationException {
        final Object enumChatString = CacheRegistry.ENUM_CHAT_TO_STRING_METHOD.invoke(npc.getGlowColor());
        if (Utils.BUKKIT_VERSION > 12) {
            Utils.setValue(packet, "g", npc.getGlowColor());
            Utils.setValue(packet, "c", CacheRegistry.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance(enumChatString));
        } else {
            Utils.setValue(packet, "g", CacheRegistry.GET_ENUM_CHAT_ID_METHOD.invoke(npc.getGlowColor()));
            Utils.setValue(packet, "c", enumChatString);
        }
    }

    @Override
    public boolean allowGlowColor() {
        return true;
    }
}
