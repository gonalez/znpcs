package io.github.znetworkw.znpcservers.cache;

/**
 * The possible packages categories when building a {@link TypeCache.CacheBuilder}.
 */
public enum CacheCategory {
    DEFAULT(),
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
     * The sub-package name.
     */
    private final String subPackageName;

    /**
     * The package name.
     */
    private final String packageName;

    /**
     * Creates a new sub-package identification.
     *
     * @param subPackageName The sub-package name.
     */
    CacheCategory(String subPackageName) {
        this.subPackageName = subPackageName;
        StringBuilder stringBuilder = new StringBuilder(CachePackage.MINECRAFT_SERVER.getFixedPackageName());
        if (subPackageName.length() > 0) {
            stringBuilder.append(".");
            stringBuilder.append(subPackageName);
        }
        this.packageName = stringBuilder.toString();
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
    public String getSubPackageName() {
        return subPackageName;
    }

    /**
     * Returns the package name.
     *
     * @return The package name.
     */
    public String getPackageName() {
        return packageName;
    }
}