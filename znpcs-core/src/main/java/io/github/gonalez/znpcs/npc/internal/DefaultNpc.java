package io.github.gonalez.znpcs.npc.internal;

import io.github.gonalez.znpcs.ZNPCs;
import io.github.gonalez.znpcs.entity.PluginEntityFactory;
import io.github.gonalez.znpcs.npc.AbstractNpc;
import io.github.gonalez.znpcs.npc.NpcClickHandler;
import io.github.gonalez.znpcs.npc.NpcModel;
import io.github.gonalez.znpcs.npc.NpcName;

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
