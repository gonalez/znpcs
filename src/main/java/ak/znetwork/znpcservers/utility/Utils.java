package ak.znetwork.znpcservers.utility;

import ak.znetwork.znpcservers.types.ConfigTypes;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.math.NumberUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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

    /**
     * Represents if the plugin will use external placeholders.
     */
    public static boolean PLACEHOLDER_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

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
     * Automatically converts string to colored string.
     *
     * @param string The string to translate.
     * @return Translated string.
     */
    public static String toColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Parses the given string for the player.
     *
     * @param player The player to parse the placeholder for.
     * @param string The string to parse.
     * @return The parsed {@link java.lang.String}.
     */
    public static String getWithPlaceholders(Player player, String string) {
        return PlaceholderAPI.setPlaceholders(player, string).replace(ConfigTypes.SPACE_SYMBOL, " ");
    }

    /** Default constructor */
    private Utils() {
        throw new AssertionError("This class is not intended to be initialized.");
    }
}
