package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.entity.PluginEntity;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcHologram {
    /**
     * Creates the hologram.
     *
     * @throws Exception if the hologram could not be created.
     */
    void create() throws Exception;

    /**
     * Returns all entities that were created by this hologram.
     *
     * @return all entities that were created by this hologram.
     */
    Iterable<? extends PluginEntity> getPluginEntity();
}
