package io.github.znetworkw.znpcservers.npc.packet;

import java.util.Arrays;

/** Enumerates all possible key names for a {@link PacketValue}. */
public enum ValueType {
    ARGUMENTS {
        @Override
        String resolve(String keyName, Object[] args) {
            if (args.length == 0) {
                throw new IllegalArgumentException("invalid size, must be > 0");
            }
            return keyName + Arrays.hashCode(args);
        }
    },
    DEFAULT {
        @Override
        String resolve(String keyName, Object[] args) {
            return keyName;
        }
    };

    /**
     * Resolves the key name.
     */
    abstract String resolve(String keyName, Object[] args);
}
