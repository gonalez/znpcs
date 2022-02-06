package io.github.gonalez.znpcservers.npc.internal;

import io.github.gonalez.znpcservers.npc.Npc;
import io.github.gonalez.znpcservers.npc.NpcClickHandler;
import io.github.gonalez.znpcservers.user.User;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultNpcClickHandler implements NpcClickHandler {
    @Override
    public void onClick(Npc npc, User user) {
        user.getPlayer().sendMessage("TEST");
    }
}
