package ak.znetwork.znpcservers.hologram.replacer;

import ak.znetwork.znpcservers.utility.PlaceholderUtils;
import ak.znetwork.znpcservers.utility.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 21/7/2021
 */
public interface LineReplacer<T> {

    /**
     * Replaces the string with the provided feature.
     *
     * @param string The string to replace.
     * @return The converted {@link java.lang.String}
     */
    T make(String string);

    /**
     * Replaces the string with the provided features.
     *
     * @param player The player to get placeholders for.
     * @param line The line to convert.
     * @return The converted {@link java.lang.String}
     */
    static String makeAll(Player player,
                          String line) {
        if (Utils.BUKKIT_VERSION > 16) {
            line = new RGBLine().make(line);
        }
        return ChatColor.translateAlternateColorCodes('&', Utils.PLACEHOLDER_SUPPORT && player != null ?
                PlaceholderUtils.getWithPlaceholders(player, line) :
                line
        );
    }
}
