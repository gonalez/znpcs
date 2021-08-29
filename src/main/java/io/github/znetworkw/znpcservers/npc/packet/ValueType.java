package io.github.znetworkw.znpcservers.npc.packet;

import io.github.znetworkw.znpcservers.npc.NPC;

import java.util.Arrays;

/** Enumerates all possible key names for a {@link PacketValue}. */
public enum ValueType {
    NPC {
        @Override
        public String resolve(String keyName, NPC npc, Object[] args) {
            return keyName + SEPARATOR + npc.getNpcPojo().getId();
        }
    },
    ARGUMENTS {
        @Override
        public String resolve(String keyName, NPC npc, Object[] args) {
            if (args.length == 0) {
                throw new IllegalArgumentException("invalid size, must be > 0");
            }
            return keyName + SEPARATOR + Arrays.toString(args);
        }
    },
    DEFAULT {
        @Override
        public String resolve(String keyName, NPC npc, Object[] args) {
            return keyName;
        }
    };

    /** default key name separator char. */
    private static final char SEPARATOR = '%';

    /**
     * Resolves the key name.
     */
    public abstract String resolve(String keyName, NPC npc, Object[] args);
}
