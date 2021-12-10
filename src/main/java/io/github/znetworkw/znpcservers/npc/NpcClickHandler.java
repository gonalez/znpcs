package io.github.znetworkw.znpcservers.npc;

import io.github.znetworkw.znpcservers.npc.internal.DefaultNpcClickHandler;
import io.github.znetworkw.znpcservers.user.User;

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
