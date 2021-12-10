package io.github.znetworkw.znpcservers.npc;

import io.github.znetworkw.znpcservers.npc.internal.PluginNpcStore;

/**
 * Interface for storing and retrieving {@link Npc}s.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcStore {
    /**
     * Creates a new, default npc store.
     *
     * @return a new npc store.
     */
    static NpcStore of() {
        return new PluginNpcStore();
    }

    /**
     * Initializes this npc store.
     *
     * @throws Exception if cannot initialize this store.
     */
    void init() throws Exception;

    Npc getNpc(int id);

    void addNpc(int id, Npc npc);

    void removeNpc(int id);

    /**
     * Retrieves all npcs in this storage,
     * the order of iteration is undefined.
     *
     * @return all npcs in this storage.
     */
    Iterable<Npc> getNpcs();
}
