package io.github.znetworkw.znpcservers.npc.function;

import io.github.znetworkw.znpcservers.UnexpectedCallException;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.npc.NPCFunction;
import io.github.znetworkw.znpcservers.npc.NPCFunctionFactory;

public class GlowFunction extends NPCFunction {
    @Override
    protected void function(NPC npc, String data) {
        try {
            // find the glow color enum
            final Object glowColor = CacheRegistry.ENUM_CHAT_FORMAT_FIND.invoke(null, data == null || data.length() == 0 ? "WHITE" : data);
            npc.setGlowColor(glowColor);
            CacheRegistry.SET_DATA_WATCHER_METHOD.invoke(CacheRegistry.GET_DATA_WATCHER_METHOD.invoke(npc.getNmsEntity()),
                CacheRegistry.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(
                    0,
                    npc.getDataWatcherRegistryEnum()),
                NPCFunctionFactory.isTrue(npc, this) ? (byte) 0x40 : (byte) 0x0);
            npc.getNpcPojo().setGlowName(data);
            // update glow scoreboard packets
            npc.getPackets().getProxyInstance().update(npc.getPackets());
            npc.deleteViewers();
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    @Override
    public String name() {
        return "glow";
    }

    @Override
    public boolean allow(NPC npc) {
        return npc.getPackets().getProxyInstance().allowGlowColor();
    }
}
