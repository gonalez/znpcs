package io.github.znetworkw.znpcservers.npc.internal;

import io.github.znetworkw.znpcservers.npc.Npc;
import io.github.znetworkw.znpcservers.npc.NpcClickHandler;
import io.github.znetworkw.znpcservers.user.User;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultNpcClickHandler implements NpcClickHandler {
    @Override
    public void onClick(Npc npc, User user) {
        user.getPlayer().sendMessage("TEST");
    }
}
