package io.github.gonalez.znpcservers.npc.internal.plugin;

import com.google.common.collect.ImmutableList;
import io.github.gonalez.znpcservers.cache.CacheRegistry;
import io.github.gonalez.znpcservers.npc.Npc;
import io.github.gonalez.znpcservers.npc.function.NpcFunctionContext;
import io.github.gonalez.znpcservers.npc.function.NpcFunctionValue;
import io.github.gonalez.znpcservers.npc.internal.function.SimpleValidateSavedNpcFunction;

/**
 * Supports glow for npcs.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class GlowNpcFunction extends SimpleValidateSavedNpcFunction {
    public GlowNpcFunction() {
        super("glow", ImmutableList.of(
            new NpcFunctionValue.DefaultValue<>("data", String.class),
            new NpcFunctionValue.DefaultValue<>("enabled",  Boolean.class)));
    }

    @Override
    protected void function(Npc npc, NpcFunctionContext functionContext) throws Exception {
        final String glowColorName = (String) functionContext.getAttribute("glowName");
        final Object glowColor = CacheRegistry.ENUM_CHAT_FORMAT_FIND.invoke(null,
            glowColorName == null || glowColorName.length() == 0 ? "WHITE" : glowColorName);
        CacheRegistry.SET_DATA_WATCHER_METHOD.invoke(CacheRegistry.GET_DATA_WATCHER_METHOD.invoke(npc),
            CacheRegistry.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(
                0, CacheRegistry.DATA_WATCHER_REGISTER_FIELD),
            (Boolean) functionContext.getAttribute("enabled") ? (byte) 0x40 : (byte) 0x0);
        //npc.getPackets().getCache().removeResult("scoreboardPackets");
        super.function(npc, functionContext); /*self update run*/
    }
}
