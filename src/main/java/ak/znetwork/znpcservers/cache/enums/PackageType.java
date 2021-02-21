package ak.znetwork.znpcservers.cache.enums;

import ak.znetwork.znpcservers.utility.ReflectionUtils;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public enum PackageType {

    /**
     * Default package
     */
    DEFAULT(""),

    /**
     * Craft Bukkit package
     */
    CRAFT_BUKKIT("org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage()),

    /**
     * Minecraft Server package
     */
    MINECRAFT_SERVER("net.minecraft.server." + ReflectionUtils.getBukkitPackage());

    /**
     * The package name.
     */
    private final String packageName;

    /**
     * Defines a new package.
     *
     * @param packageName The package name.
     */
    PackageType(String packageName) {
        this.packageName = packageName;
    }
}
