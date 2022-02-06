package io.github.gonalez.znpcservers.npc.internal;

import io.github.gonalez.znpcservers.ZNPCs;
import io.github.gonalez.znpcservers.entity.PluginEntityFactory;
import io.github.gonalez.znpcservers.npc.AbstractNpc;
import io.github.gonalez.znpcservers.npc.NpcClickHandler;
import io.github.gonalez.znpcservers.npc.NpcModel;
import io.github.gonalez.znpcservers.npc.NpcName;
import io.github.gonalez.znpcservers.npc.*;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultNpc extends AbstractNpc {
    public DefaultNpc(PluginEntityFactory<?> pluginEntityFactory, NpcModel npcModel,
                      NpcName npcName, NpcClickHandler clickHandler) {
        super(pluginEntityFactory, npcModel, npcName, clickHandler, ZNPCs.SETTINGS.getTaskManager());
    }

    @Override
    protected void load() {}
}
