package io.github.gonalez.znpcs.npc.internal;

import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.npc.NpcName;
import io.github.gonalez.znpcs.utility.Utils;

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
