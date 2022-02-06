package io.github.gonalez.znpcservers.npc.internal;

import io.github.gonalez.znpcservers.npc.Npc;
import io.github.gonalez.znpcservers.npc.NpcName;
import io.github.gonalez.znpcservers.utility.Utils;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultNpcName implements NpcName {
    public static final NpcName INSTANCE = new DefaultNpcName();

    @Override
    public String generateNpcName(Npc npc) {
        return Utils.randomString(6);
    }
}
