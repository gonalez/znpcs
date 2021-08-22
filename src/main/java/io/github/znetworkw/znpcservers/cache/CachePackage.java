package io.github.znetworkw.znpcservers.cache;

import io.github.znetworkw.znpcservers.utility.Utils;

/**
 * The possible packages when building a {@link TypeCache.CacheBuilder}.
 */
public enum CachePackage {
    /**
     * Default package.
     */
    DEFAULT(),
    /**
     * Craft bukkit package.
     */
    CRAFT_BUKKIT("org.bukkit.craftbukkit." + Utils.getBukkitPackage()),
    /**
     * Minecraft server package.
     */
    MINECRAFT_SERVER("net.minecraft");

    /**
     * A empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * A dot string.
     */
    private static final String DOT = ".";

    /**
     * The fixed package name.
     */
    private final String fixedPackageName;

    /**
     * Defines a new package.
     *
     * @param packageName The package name.
     */
    CachePackage(String packageName) {
        this.fixedPackageName = Utils.BUKKIT_VERSION > 16 ? // v1.17+
                packageName :
                packageName + (packageName.contains("minecraft") ? DOT + "server" + DOT + Utils.getBukkitPackage() : EMPTY_STRING);
    }

    /**
     * Defines a new empty package.
     */
    CachePackage() {
        this.fixedPackageName = EMPTY_STRING;
    }

    /**
     * Locates a package by its category.
     *
     * @param packetCategory The packet category.
     * @return The package name for category.
     */
    public String getForCategory(CacheCategory packetCategory,
                                 String extra) {
        return Utils.BUKKIT_VERSION > 16 ?
                fixedPackageName + packetCategory.getSubPackageName() + (extra.length() > 0 ? DOT + extra : EMPTY_STRING) :
                fixedPackageName;
    }

    /**
     * Returns the fixed package name for the current version.
     *
     * @return The fixed package name for the current version.
     */
    public String getFixedPackageName() {
        return fixedPackageName;
    }
}