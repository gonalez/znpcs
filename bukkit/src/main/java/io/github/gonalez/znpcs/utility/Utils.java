package io.github.gonalez.znpcs.utility;

import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.configuration.ConfigConfiguration;
import io.github.gonalez.znpcs.user.ZUser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

public final class Utils {
  public static final int BUKKIT_VERSION;
  public static boolean PLACEHOLDER_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
  
  static {
    BUKKIT_VERSION = NumberUtils.toInt(getFormattedBukkitPackage());
  }

  public static void sendMessage(CommandSender commandSender, String message, Object... args) {
    commandSender.sendMessage(toColor(String.format(message, args)));
  }

  public static boolean versionNewer(int version) {
    return (BUKKIT_VERSION >= version);
  }
  
  public static String getBukkitPackage() {
    return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
  }
  
  public static String getFormattedBukkitPackage() {
    String version = getBukkitPackage().replace("v", "").replace("R", "");
    return version.substring(2, version.length() - 2);
  }
  
  public static String toColor(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }
  
  public static String getWithPlaceholders(String string, Player player) {
    return PlaceholderAPI.setPlaceholders(player, string).replace(
        ZNPConfigUtils.getConfig(ConfigConfiguration.class).replaceSymbol, " ");
  }
  
  public static String randomString(int length) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int index = 0; index < length; index++)
      stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9)); 
    return stringBuilder.toString();
  }
  
  public static void sendTitle(Player player, String title, String subTitle) {
    player.sendTitle(toColor(title), toColor(subTitle));
  }
  
  public static void setValue(Object fieldInstance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
    Field f = fieldInstance.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(fieldInstance, value);
  }

  public static void setValue(
      Object fieldInstance, Object value, Class<?> expectedType)
      throws NoSuchFieldException, IllegalAccessException {
    for (Field field : fieldInstance.getClass().getDeclaredFields()) {
      if (field.getType() == expectedType)
        setValue(fieldInstance, field.getName(), value);
    }
  }

  public static Object getValue(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    Field f = instance.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    return f.get(instance);
  }
  
  public static void sendPackets(ZUser user, Object... packets) {
    try {
      for (Object packet : packets) {
        if (packet != null)
          CacheRegistry.SEND_PACKET_METHOD.load().invoke(user.getPlayerConnection(), packet);
      } 
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      e.printStackTrace();
    } 
  }
}
