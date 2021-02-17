/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package ak.znetwork.znpcservers.utils;

import ak.znetwork.znpcservers.cache.ClazzCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
     * Sends the packet to the receiver
     *
     * @param player receiver
     * @param packet packet to send
     */
    public static void sendPacket(final Player player, final Object packet) {
        try {
            ClazzCache.SEND_PACKET_METHOD.getCacheMethod().invoke(ClazzCache.PLAYER_CONNECTION_FIELD.getCacheField().get(ClazzCache.GET_HANDLE_PLAYER_METHOD.getCacheMethod().invoke(player)), packet);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets current bukkit version
     *
     * @return bukkit version name
     */
    public static String getBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Gets friendly current bukkit name version
     *
     * @return bukkit friendly version name
     */
    public static String getFriendlyBukkitPackage() {
        String version = getBukkitPackage().replace("v", "").replace("R", "");

        return version.substring(2, version.length() - 2);
    }
}
