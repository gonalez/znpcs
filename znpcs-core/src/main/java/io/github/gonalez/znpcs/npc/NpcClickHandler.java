package io.github.gonalez.znpcs.npc;

import io.github.gonalez.znpcs.npc.internal.DefaultNpcClickHandler;
import io.github.gonalez.znpcs.user.User;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcClickHandler {
    static NpcClickHandler of() {
        return new DefaultNpcClickHandler();
    }

    /**
     * Called when the user clicks the npc.
     *
     * @param npc the clicked npc.
     * @param user the user that clicked the npc.
     */
    void onClick(Npc npc, User user);
}
