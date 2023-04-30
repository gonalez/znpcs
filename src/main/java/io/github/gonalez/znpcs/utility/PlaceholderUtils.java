package io.github.gonalez.znpcs.utility;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

public final class PlaceholderUtils {

  public static String formatPlaceholders(String str) {
    return Bukkit.getOnlinePlayers().stream()
        .findAny()
        .map(player -> {
          return PlaceholderAPI.setPlaceholders(player, str);
        }).orElse(str);
  }
}
