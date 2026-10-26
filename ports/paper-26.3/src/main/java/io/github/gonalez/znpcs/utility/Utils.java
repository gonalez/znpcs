/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.utility;

import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.user.ZUser;
import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.clip.placeholderapi.PlaceholderAPI;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class Utils {
  public static final Version BUKKIT_VERSION;
  public static final boolean PLACEHOLDER_SUPPORT;

  public static double extractAndSumNumbers(String str) {
    Matcher matcher = Pattern.compile("^(?:v)?(\\d+)\\.(\\d+)(?:\\.(\\d+))?.*").matcher(str);
    if (!matcher.matches()) throw new IllegalArgumentException("Invalid Minecraft version: " + str);
    int major = Integer.parseInt(matcher.group(1));
    int minor = Integer.parseInt(matcher.group(2));
    int patch = matcher.group(3) == null ? 0 : Integer.parseInt(matcher.group(3));
    return major == 1
        ? minor + patch / Math.pow(10, Integer.toString(patch).length())
        : major + minor / 10.0;
  }

  public static boolean isVersionNew(double version) {
    return isVersionSameOrNew(Double.toString(version));
  }

  public static String getBukkitPackage() {
    String packageName = Bukkit.getServer().getClass().getPackage().getName();
    String[] split = packageName.split("\\.");
    if (split.length > 3) {
      return split[3];
    }
    return "";
  }

  public static String toColor(String string) {
    return ChatColor.translateAlternateColorCodes((char) '&', (String) string);
  }

  public static String getWithPlaceholders(String string, Player player) {
    String result = PLACEHOLDER_SUPPORT ? PlaceholderAPI.setPlaceholders(player, string) : string;
    return result.replace(ConfigurationConstants.SPACE_SYMBOL, " ");
  }

  public static String randomString(int length) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int index = 0; index < length; ++index) {
      stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9));
    }
    return stringBuilder.toString();
  }

  public static void sendTitle(Player player, String title, String subTitle) {
    player.sendTitle(Utils.toColor(title), Utils.toColor(subTitle));
  }

  public static void setValue(Object fieldInstance, String fieldName, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    Field f = fieldInstance.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(fieldInstance, value);
  }

  public static void setValue(Object fieldInstance, Object value, Class<?> expectedType)
      throws NoSuchFieldException, IllegalAccessException {
    for (Field field : fieldInstance.getClass().getDeclaredFields()) {
      if (field.getType() != expectedType) continue;
      Utils.setValue(fieldInstance, field.getName(), value);
    }
  }

  public static Object getValue(Object instance, String fieldName)
      throws NoSuchFieldException, IllegalAccessException {
    Field f = instance.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    return f.get(instance);
  }

  public static void sendPackets(ZUser user, Object... packets) {
    Player player = user.toPlayer();
    if (player == null || !player.isOnline()) return;
    for (Object packet : packets) {
      if (packet != null) ((CraftPlayer) player).getHandle().connection.send((Packet<?>) packet);
    }
  }

  static {
    PLACEHOLDER_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    BUKKIT_VERSION = Version.fromBukkit();
  }

  public static boolean isVersionSameOrNew(String version) {
    return BUKKIT_VERSION.compareTo(Version.fromString(version)) >= 0;
  }

  public static boolean isVersionSameOrNew(int version) {
    return isVersionSameOrNew(Integer.toString(version));
  }

  public static boolean isPaper() {
    return true;
  }
}
