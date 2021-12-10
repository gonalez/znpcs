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

    /**
     * Retrieves the npc with the given id.
     *
     * @param id the npc id.
     * @return
     *    the npc matching the given id, or
     *    {@code null} if no match npc was found.
     */
    Npc getNpc(int id);

    /**
     * Adds a new npc to this storage.
     *
     * @param id the npc id.
     * @param npc the npc to add.
     * @return added npc.
     */
    Npc addNpc(int id, Npc npc);

    /**
     * Removes the npc matching the given id from the storage.
     *
     * @param id the npc id.
     */
    void removeNpc(int id);

    /**
     * Retrieves all npcs in this storage, the order of iteration is undefined.
     *
     * @return all npcs in this storage.
     */
    Iterable<Npc> getNpcs();
}
