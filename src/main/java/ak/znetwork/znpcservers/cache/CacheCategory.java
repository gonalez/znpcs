package ak.znetwork.znpcservers.cache;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public enum CacheCategory {
    NONE(),
    NETWORK("network"),
    PROTOCOL("network.protocol"),
    CHAT("network.chat"),
    PACKET("network.protocol.game"),
    SYNCHER("network.syncher"),
    ENTITY("world.entity"),
    ITEM("world.item"),
    WORLD_LEVEL("world.level"),
    WORLD_SCORES("world.scores"),
    SERVER_LEVEL("server.level"),
    SERVER_NETWORK("server.network"),
    SERVER("server");

    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * A dot string.
     */
    private static final String DOT = ".";

    /**
     * The sub-package name.
     */
    private final String subPackageName;

    /**
     * Creates a new sub-package identification.
     *
     * @param packageIndex The sub-package name.
     */
    CacheCategory(String packageIndex) {
        this.subPackageName = packageIndex;
    }

    /**
     * Creates a new empty sub-package identification.
     */
    CacheCategory() {
        this(EMPTY_STRING);
    }

    /**
     * Returns the sub-package name.
     *
     * @return The sub-package name.
     */
    protected String getSubPackageName() {
        return this == NONE ? EMPTY_STRING : DOT + subPackageName;
    }
}