package io.github.znetworkw.znpcservers.utility;

import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.configuration.ConfigTypes;
import io.github.znetworkw.znpcservers.user.ZUser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Helper functions for the plugin.
 */
public final class Utils {
    /** The server bukkit version. */
    public static final int BUKKIT_VERSION;

    /** Represents one second in nanos */
    public static final long SECOND_INTERVAL_NANOS = 1000 * 1000 * 1000L;

    /** Returns {@code true} if the plugin should use external placeholders. */
    public static boolean PLACEHOLDER_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    static {
        BUKKIT_VERSION = NumberUtils.toInt(getFormattedBukkitPackage());
    }

    /**
     * Returns {@code true} if the given version is newer than the current bukkit version.
     *
     * @param version The version to compare.
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
     * Returns the formatted bukkit name version.
     */
    public static String getFormattedBukkitPackage() {
        final String version = getBukkitPackage().replace("v", "").replace("R", "");
        return version.substring(2, version.length() - 2);
    }

    /**
     * Automatically converts the given string to a bukkit colored string.
     *
     * @param string The string to convert.
     * @return The converted string.
     */
    public static String toColor(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * Parses the given string for the player.
     *
     * @param string The string to parse.
     * @param player The player to parse the string for.
     * @return The parsed string.
     */
    public static String getWithPlaceholders(String string, Player player) {
        return PlaceholderAPI.setPlaceholders(player, string).replace(ConfigTypes.SPACE_SYMBOL, " ");
    }

    /**
     * Creates a random {@link java.lang.String} with the specified character {@code length}.
     *
     * @return A random {@link java.lang.String} with the specified {@code length}.
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
     * @param player The player to send the title for.
     * @param title The title string.
     * @param subTitle The subtitle string.
     */
    public static void sendTitle(Player player,
                                 String title,
                                 String subTitle) {
        player.sendTitle(toColor(title), toColor(subTitle));
    }

    /**
     * Sets the new value for the field.
     *
     * @param fieldInstance The field instance.
     * @param fieldName The field name.
     * @param value The new field value.
     * @throws NoSuchFieldException If the field could not be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     */
    public static void setValue(
            Object fieldInstance,
            String fieldName,
            Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = fieldInstance.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(fieldInstance, value);
    }

    /**
     * Locates the specified field value on the given instance.
     *
     * @param instance The field instance.
     * @param fieldName The field name.
     * @return The field value.
     * @throws NoSuchFieldException If the field could not be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     */
    public static Object getValue(
            Object instance,
            String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = instance.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        return f.get(instance);
    }

    /**
     * Sends the given packets to the given player.
     *
     * @param user The player to send the packets for.
     * @param packets The packets to send.
     */
    public static void sendPackets(ZUser user, Object... packets) {
        try {
            for (Object packet : packets) {
                CacheRegistry.SEND_PACKET_METHOD.invoke(user.getPlayerConnection(), packet);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /** Default constructor */
    private Utils() {
        throw new AssertionError("This class is not intended to be initialized.");
    }
}
