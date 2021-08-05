package ak.znetwork.znpcservers.hologram.replacer;

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
     * Replaces the {@link java.lang.String} with the line replaces.
     *
     * @param string The {@link java.lang.String} to replace.
     * @return The converted {@link java.lang.String}.
     */
    T make(String string);

    /**
     * Replaces the {@link java.lang.String} with the line replaces.
     *
     * @param player The player to get placeholders for.
     * @param string The {@link java.lang.String} to convert.
     * @return The converted {@link java.lang.String}.
     */
    static String makeAll(Player player,
                          String string) {
        if (Utils.BUKKIT_VERSION > 15) { // v1.16+
            string = new RGBLine().make(string);
        }
        return ChatColor.translateAlternateColorCodes('&', Utils.PLACEHOLDER_SUPPORT && player != null ?
                Utils.getWithPlaceholders(player, string) :
                string
        );
    }
}
