package io.github.znetworkw.znpcservers.npc.function;

import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.npc.FunctionContext;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.npc.NPCFunction;
import io.github.znetworkw.znpcservers.npc.FunctionFactory;

public class GlowFunction extends NPCFunction {
    public GlowFunction() {
        super("glow");
    }

    @Override
    protected ResultType runFunction(NPC npc, FunctionContext functionContext) {
        if (!(functionContext instanceof FunctionContext.ContextWithValue)) {
            throw new IllegalStateException("invalid context type, " + functionContext.getClass().getSimpleName() + ", expected ContextWithValue.");
        }
        final String glowColorName = ((FunctionContext.ContextWithValue) functionContext).getValue();
        try {
            // find the glow color enum
            final Object glowColor = CacheRegistry.ENUM_CHAT_FORMAT_FIND.invoke(null,
                glowColorName == null || glowColorName.length() == 0 ? "WHITE" : glowColorName);
            npc.setGlowColor(glowColor);
            CacheRegistry.SET_DATA_WATCHER_METHOD.invoke(CacheRegistry.GET_DATA_WATCHER_METHOD.invoke(npc.getNmsEntity()),
                CacheRegistry.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(
                    0,
                    CacheRegistry.DATA_WATCHER_REGISTER_FIELD),
                !FunctionFactory.isTrue(npc, this) ? (byte) 0x40 : (byte) 0x0);
            // update glow scoreboard packets
            npc.getPackets().getProxyInstance().update(npc.getPackets());
            npc.deleteViewers();
            return ResultType.SUCCESS;
        } catch (ReflectiveOperationException operationException) {
            return ResultType.FAIL;
        }
    }

    @Override
    protected boolean allow(NPC npc) {
        return npc.getPackets().getProxyInstance().allowGlowColor();
    }

    @Override
    public ResultType resolve(NPC npc) {
        return runFunction(npc, new FunctionContext.ContextWithValue(npc, npc.getNpcPojo().getGlowName()));
    }
}
