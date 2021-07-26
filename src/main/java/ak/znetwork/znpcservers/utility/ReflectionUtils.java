package ak.znetwork.znpcservers.utility;

import ak.znetwork.znpcservers.types.ClassTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class ReflectionUtils {
    /**
     *
     *
     * @param instance                The class instance.
     * @param fieldName               The field Name.
     * @param value
     * @throws NoSuchFieldException   If the field could not be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     */
    public static void setValue(
            Object instance,
            String fieldName,
            Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = instance.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(instance, value);
    }

    /**
     * Locates the specified field value on the given instance.
     *
     * @param instance                The class instance.
     * @param fieldName               The field Name.
     * @throws NoSuchFieldException   If the field could not be found.
     * @throws IllegalAccessException If the field cannot be accessed.
     * @return The field value.
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
     * Sends the packet to the given player.
     *
     * @param player  The player to send the packets for.
     * @param packets The packets to send.
     */
    public static void sendPacket(Player player, Object... packets) {
        try {
            for (Object packet : packets) {
                ClassTypes.SEND_PACKET_METHOD.invoke(ClassTypes.PLAYER_CONNECTION_FIELD.get(ClassTypes.GET_HANDLE_PLAYER_METHOD.invoke(player)), packet);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns current bukkit version.
     *
     * @return The bukkit version name.
     */
    public static String getBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Returns friendly current bukkit name version.
     *
     * @return The bukkit friendly version name.
     */
    public static String getFriendlyBukkitPackage() {
        String version = getBukkitPackage().replace("v", "").replace("R", "");
        return version.substring(2, version.length() - 2);
    }

    /** Default constructor */
    private ReflectionUtils() {
        throw new AssertionError("This class is not intended to be initialized.");
    }
}
