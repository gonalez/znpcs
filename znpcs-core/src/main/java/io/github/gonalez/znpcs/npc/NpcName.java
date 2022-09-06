package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.npc.internal.DefaultNpcName;

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
