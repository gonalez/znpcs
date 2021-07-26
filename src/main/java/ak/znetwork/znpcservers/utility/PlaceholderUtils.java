package ak.znetwork.znpcservers.utility;

import ak.znetwork.znpcservers.types.ConfigTypes;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.PlaceholderAPI;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PlaceholderUtils {
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
}
