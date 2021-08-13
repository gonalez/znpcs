package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.utility.Utils;

/**
 * Enumerates all possible names for a {@link NPC}.
 */
public enum NamingType {
    DEFAULT {
        @Override
        public String resolve(NPC npc) {
            return Utils.randomString(FIXED_LENGTH);
        }
    };

    /** Default length for tab npc name. */
    private static final int FIXED_LENGTH = 6;

    /**
     * {@inheritDoc}
     */
    public abstract String resolve(NPC npc);
}
