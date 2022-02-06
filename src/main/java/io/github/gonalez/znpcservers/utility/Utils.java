package io.github.gonalez.znpcservers.utility;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

/**
 *  Helper functions for the plugin.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public final class Utils {
    public static final int BUKKIT_VERSION = NumberUtils.toInt(getFormattedBukkitPackage());
    public static final long SECOND_INTERVAL_NANOS = 1000 * 1000 * 1000L;

    public static boolean PLACEHOLDER_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    /**
     * Returns {@code true} if the given version is newer than the current bukkit version.
     *
     * @param version the version to compare.
     * @return {@code true} if the given version is newer than the current bukkit version.
     */
    public static boolean versionNewer(int version) {
        return BUKKIT_VERSION >= version;
    }

    /**
     * Returns the current bukkit version.
     */
    public static String getBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Returns the formatted bukkit server name version.
     */
    public static String getFormattedBukkitPackage() {
        final String version = getBukkitPackage().replace("v", "").replace("R", "");
        return version.substring(2, version.length() - 2);
    }

    /**
     * Automatically converts the given string to a bukkit colored string.
     *
     * @param string the string to convert.
     * @return converted string.
     */
    public static String toColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Creates a random {@link String} with the specified character {@code length}.
     *
     * @return a random string with the specified {@code length}.
     */
    public static String randomString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9));
        }
        return stringBuilder.toString();
    }

    /**
     * Sends a title to the given player.
     *
     * @param player the player to send the title for.
     * @param title the title string.
     * @param subTitle the subtitle string.
     */
    public static void sendTitle(Player player, String title, String subTitle) {
        player.sendTitle(toColor(title), toColor(subTitle));
    }

    /**
     * Sets the new value for the field.
     *
     * @param fieldInstance the field instance.
     * @param fieldName the field name.
     * @param value the new field value.
     * @throws NoSuchFieldException if the field could not be found.
     * @throws IllegalAccessException if the field cannot be accessed.
     */
    public static void setValue(Object fieldInstance, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = fieldInstance.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(fieldInstance, value);
    }

    /**
     * Locates the specified field value on the given instance.
     *
     * @param instance the field instance.
     * @param fieldName the field name.
     * @return the field value.
     * @throws NoSuchFieldException if the field could not be found.
     * @throws IllegalAccessException if the field cannot be accessed.
     */
    public static Object getValue(Object instance, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = instance.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(instance);
    }

    private Utils() {}
}
