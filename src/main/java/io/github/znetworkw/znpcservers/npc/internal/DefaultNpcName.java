package io.github.znetworkw.znpcservers.npc.internal;

import io.github.znetworkw.znpcservers.npc.Npc;
import io.github.znetworkw.znpcservers.npc.NpcName;
import io.github.znetworkw.znpcservers.utility.Utils;

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
