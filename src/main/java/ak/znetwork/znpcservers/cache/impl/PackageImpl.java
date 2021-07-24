package ak.znetwork.znpcservers.cache.impl;

import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public interface PackageImpl {

    /**
     * An empty string.
     */
    String EMPTY_STRING = "";

    String DOT = ".";

    /**
     * The sub-packages names for 1.17+
     */
    enum PacketCategory {
        NONE(EMPTY_STRING),
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
         * The sub-package name.
         */
        private final String subPackageName;

        /**
         * Creates a new sub-package identification.
         *
         * @param packageIndex The sub-package name.
         */
        PacketCategory(String packageIndex) {
            this.subPackageName = packageIndex;
        }

        /**
         * Returns the sub-package name.
         *
         * @return The sub-package name.
         */
        private String getSubPackageName() {
            return this == NONE ? EMPTY_STRING : DOT + subPackageName;
        }
    }

    /**
     * The bukkit packages.
     */
    enum TypePackage implements PackageImpl {
        /**
         * Default package.
         */
        DEFAULT(EMPTY_STRING),

        /**
         * Craft Bukkit package.
         */
        CRAFT_BUKKIT("org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage()),

        /**
         * Minecraft Server package for > 1.17.
         */
        MINECRAFT_SERVER("net.minecraft");

        /**
         * The fixed package name.
         */
        private final String fixedPackageName;

        /**
         * Defines a new package.
         *
         * @param packageName The package name.
         */
        TypePackage(String packageName) {
            this.fixedPackageName = Utils.BUKKIT_VERSION > 16 ? // v1.17+
                    packageName :
                    DOT + "server" + DOT + ReflectionUtils.getBukkitPackage();
        }

        /**
         * Locates a package by its category.
         *
         * @param packetCategory The packet category.
         * @return The package name for category.
         */
        public String getForCategory(PacketCategory packetCategory,
                                            String extra) {
            return Utils.BUKKIT_VERSION > 16 ?
                    fixedPackageName + packetCategory.getSubPackageName() +
                            (extra.length() > 0 ? DOT + extra : EMPTY_STRING)
                    : fixedPackageName;
        }

        /**
         * Locates a package by its category.
         *
         * @param packetCategory The packet category.
         * @return The package name for category.
         */
        public String getForCategory(PacketCategory packetCategory) {
            return getForCategory(packetCategory,
                    EMPTY_STRING);
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
}
