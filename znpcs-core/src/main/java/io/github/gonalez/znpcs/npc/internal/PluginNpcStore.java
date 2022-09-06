package io.github.gonalez.znpcs.npc.internal;

import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.entity.PluginEntityFactory;
import io.github.gonalez.znpcs.npc.*;

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
