package io.github.znetworkw.znpcservers.npc.internal;

import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.entity.PluginEntityFactory;
import io.github.znetworkw.znpcservers.npc.*;
import io.github.znetworkw.znpcservers.user.User;

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
