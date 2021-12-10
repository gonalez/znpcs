package io.github.znetworkw.znpcservers.cache;

/**
 * The possible packages categories when building a {@link TypeCache.CacheBuilder}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public enum CacheCategory {
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

    /** The sub-package name. */
    private final String subPackageName;
    /** The package name. */
    private final String packageName;

    /**
     * Creates a new sub-package identification.
     *
     * @param subPackageName The sub-package name.
     */
    CacheCategory(String subPackageName) {
        this.subPackageName = subPackageName;
        this.packageName = CachePackage.MINECRAFT_SERVER.getFixedPackageName() + "." + subPackageName;
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
