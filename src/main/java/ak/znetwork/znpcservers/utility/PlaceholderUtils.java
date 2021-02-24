package ak.znetwork.znpcservers.utility;

import ak.znetwork.znpcservers.types.ConfigTypes;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PlaceholderUtils {

    public static String getWithPlaceholders(Player player, String get) {
        return PlaceholderAPI.setPlaceholders(player, get).replace(ConfigTypes.SPACE_SYMBOL, " ");
    }
}
