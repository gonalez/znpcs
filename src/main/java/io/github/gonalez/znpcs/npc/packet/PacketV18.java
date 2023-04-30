package io.github.gonalez.znpcs.npc.packet;

import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.utility.Utils;

public class PacketV18 extends PacketV17 {

  @Override
  public int version() {
    return 18;
  }

  public void updateGlowPacket(NPC npc, Object packet) throws ReflectiveOperationException {
    Utils.setValue(packet, "m", CacheRegistry.ENUM_CHAT_FORMAT_FIND.load().invoke(null, npc.getNpcPojo().getGlowName()));
  }
}
