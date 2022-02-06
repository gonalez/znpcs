package io.github.gonalez.znpcservers.cache;

import io.github.gonalez.znpcservers.utility.Utils;

/**
 * The possible packages when building a {@link TypeCache.CacheBuilder}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public enum CachePackage {
    /** Default package. */
    DEFAULT(""),
    /** Craft bukkit package. */
    CRAFT_BUKKIT("org.bukkit.craftbukkit." + Utils.getBukkitPackage()),
    /** Minecraft server package for v1.17+ versions. */
    MINECRAFT_SERVER("net.minecraft"),
    /** Minecraft server package for 1.8-1.16 versions. */
    MINECRAFT_SERVER_V2("net.minecraft.server." + Utils.getBukkitPackage());

    /** The fixed package name. */
    private final String fixedPackageName;

    /**
     * Defines a new package.
     *
     * @param packageName The package name.
     */
    CachePackage(String packageName) {
        this.fixedPackageName = packageName;
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
