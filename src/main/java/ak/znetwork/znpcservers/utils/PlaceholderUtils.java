package ak.znetwork.znpcservers.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderUtils {

    public static String getWithPlaceholders(final Player player , final String get) {
        return PlaceholderAPI.setPlaceholders(player , get);
    }
}