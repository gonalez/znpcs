package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.utility.Utils;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum NamingType {
    /** Default */
    DEFAULT {
        @Override
        public String resolve(ZNPC znpc) {
            return Utils.randomString(FIXED_LENGTH);
        }
    };

    /**
     * Default length for tab npc name.
     */
    private static final int FIXED_LENGTH = 6;

    /**
     * {@inheritDoc}
     */
    public abstract String resolve(ZNPC znpc);
}
