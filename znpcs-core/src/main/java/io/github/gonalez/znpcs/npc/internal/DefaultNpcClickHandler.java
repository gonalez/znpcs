package io.github.gonalez.znpcs.npc.internal;

import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.npc.NpcClickHandler;
import io.github.gonalez.znpcs.user.User;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultNpcClickHandler implements NpcClickHandler {
    @Override
    public void onClick(Npc npc, User user) {
        user.getPlayer().sendMessage("TEST");
    }
}
