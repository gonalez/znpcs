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
public class ReflectionUtils {

    public static void setValue(Object instance, String field, Object value) throws IllegalAccessException, NoSuchFieldException {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        f.set(instance, value);
    }

    public static Object getValue(Object instance, String field) throws NoSuchFieldException, IllegalAccessException {
        Field f = instance.getClass().getDeclaredField(field);
        f.setAccessible(true);
        return f.get(instance);
    }

    /**
     * Sends the packet to a receiver.
     *
     * @param player  The receiver of the packet.
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
     * Gets current bukkit version.
     *
     * @return The bukkit version name.
     */
    public static String getBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Gets friendly current bukkit name version.
     *
     * @return The bukkit friendly version name.
     */
    public static String getFriendlyBukkitPackage() {
        String version = getBukkitPackage().replace("v", "").replace("R", "");
        return version.substring(2, version.length() - 2);
    }
}
