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

    /**
     * ...
     */
    String DOT = ".";

    /**
     * 1.17+
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
         * The package name.
         */
        private final String packageName;

        /**
         * Creates a new package identification.
         *
         * @param packageIndex The package name.
         */
        PacketCategory(String packageIndex) {
            this.packageName = packageIndex;
        }
    }

    /**
     * @inheritDoc
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
         * The package name.
         */
        private final String packageName;

        /**
         * Defines a new package.
         *
         * @param packageName The package name.
         */
        TypePackage(String packageName) {
            this.packageName = packageName;
        }

        /**
         * The default package name.
         */
        public String getPackageName() {
            StringBuilder stringBuilder = new StringBuilder(packageName);
            final boolean V17 = Utils.BUKKIT_VERSION > 16;
            if (!V17)  {
                if (this == TypePackage.MINECRAFT_SERVER) {
                    stringBuilder.append(DOT).append("server").append(DOT).append(ReflectionUtils.getBukkitPackage());
                }
            }
            return stringBuilder.toString();
        }

        /**
         * Locates a package by its category.
         *
         * @param packetCategory The packet category.
         * @return The package name for category.
         */
        public String getForCategory(PacketCategory packetCategory,
                                            String extra) {
            if (Utils.BUKKIT_VERSION < 17) { // < 1.16
                return getPackageName();
            } else { // > 1.17+
                return getPackageName() +
                        (packetCategory != PacketCategory.NONE ? DOT + packetCategory.packageName : EMPTY_STRING) +
                        (extra.length() > 0 ? DOT + extra : EMPTY_STRING);
            }
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
    }
}
