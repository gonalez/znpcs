package io.github.gonalez.znpcs.npc.packet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.ItemSlot;
import io.github.gonalez.znpcs.npc.NPC;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class PacketV16 extends PacketV9 {
  public int version() {
    return 16;
  }
  
  public ImmutableList<Object> getEquipPackets(NPC npc) throws ReflectiveOperationException {
    List<Pair<?, ?>> pairs = Lists.newArrayListWithCapacity((ItemSlot.values()).length);
    for (Map.Entry<ItemSlot, ItemStack> entry : npc.getNpcPojo().getNpcEquip().entrySet())
      pairs.add(new Pair<>(getItemSlot(entry
              .getKey().getSlot()),
            convertItemStack(npc.getEntityID(), entry.getKey(), entry.getValue()))); 
    return ImmutableList.of(CacheRegistry.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_V1.load().newInstance(npc.getEntityID(), pairs));
  }
}
