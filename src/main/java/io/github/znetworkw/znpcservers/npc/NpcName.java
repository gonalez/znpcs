package io.github.znetworkw.znpcservers.npc;

import io.github.znetworkw.znpcservers.npc.internal.DefaultNpcName;

/**
 * Interface to generate a unique name for an {@link Npc}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcName {
    static NpcName of() {
        return DefaultNpcName.INSTANCE;
    }

    /**
     * Generates a new npc name.
     *
     * @param npc the npc to generate the name for.
     * @return a new npc name.
     */
    String generateNpcName(Npc npc);
}
