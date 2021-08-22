package io.github.znetworkw.znpcservers.npc.hologram.replacer;

import io.github.znetworkw.znpcservers.user.ZUser;
import io.github.znetworkw.znpcservers.utility.Utils;
import org.bukkit.ChatColor;

/**
 * Interface used for replacing a {@link java.lang.String}.
 */
public interface LineReplacer<T> {
    /**
     * Replaces the given string.
     *
     * @param string The string to replace.
     * @return The converted {@link java.lang.String}.
     */
    T make(String string);

    /**
     * Replaces the {@link java.lang.String} with custom replaces.
     *
     * @param user The player to get placeholders for.
     * @param string The string to convert.
     * @return The converted string.
     */
    static String makeAll(ZUser user,
                          String string) {
        if (Utils.BUKKIT_VERSION > 15) { // v1.16+
            string = new RGBLine().make(string);
        }
        return ChatColor.translateAlternateColorCodes('&', Utils.PLACEHOLDER_SUPPORT && user != null ?
                Utils.getWithPlaceholders(string, user.toPlayer()) :
                string
        );
    }
}
