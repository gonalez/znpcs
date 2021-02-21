package ak.znetwork.znpcservers.utility;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class Utils {

    /**
     * The current bukkit version.
     */
    public static final int BUKKIT_VERSION;

    static {
        BUKKIT_VERSION = NumberUtils.toInt(ReflectionUtils.getFriendlyBukkitPackage());
    }

    /**
     * Checks if version is newer than current bukkit version.
     *
     * @param version The version to compare.
     * @return {@code true} If is newer version.
     */
    public static boolean versionNewer(int version) {
        return BUKKIT_VERSION >= version;
    }

    /**
     * Generates a random alpha-numeric string that is 8 characters.
     *
     * @return The Generated string.
     */
    public static String generateRandomString() {
        return RandomStringUtils.randomAlphanumeric(8).toLowerCase();
    }

    /**
     * Automatically converts string to colored string.
     *
     * @param string The string to translate.
     * @return Translated string.
     */
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
