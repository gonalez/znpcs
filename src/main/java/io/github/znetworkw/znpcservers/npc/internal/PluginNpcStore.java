package io.github.znetworkw.znpcservers.npc.internal;

import io.github.znetworkw.znpcservers.configuration.ConfigurationConstants;
import io.github.znetworkw.znpcservers.entity.PluginEntityFactory;
import io.github.znetworkw.znpcservers.npc.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PluginNpcStore implements NpcStore {
    private final Map<Integer, Npc> npcMap = new ConcurrentHashMap<>();

    protected boolean init;

    @Override
    public void init() throws Exception {
        if (init) {
            return;
        }

        init = true;
        for (NpcModel npcModel : ConfigurationConstants.NPC_MODELS) {
            addNpc(npcModel.getId(), Npc.of(PluginEntityFactory.of(), npcModel, NpcName.of(), NpcClickHandler.of()));
        }
    }

    @Override
    public Npc getNpc(int id) {
        return npcMap.get(id);
    }

    @Override
    public Npc addNpc(int id, Npc npc) {
        return npcMap.put(id, npc);
    }

    @Override
    public void removeNpc(int id) {
        npcMap.remove(id);
    }

    @Override
    public Iterable<Npc> getNpcs() {
        return npcMap.values();
    }
}
