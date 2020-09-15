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

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.ChatColor;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class Utils {

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isVersionNewestThan(int ver) {
        return getVersion() >= ver;
    }

    public static int getVersion() {
        return Integer.parseInt(ReflectionUtils.getFriendlyBukkitPackage());
    }

    public static String generateRandom()  {
        return RandomStringUtils.randomAlphanumeric(8).toLowerCase();
    }

    public static Constructor<?> getDefinedConstructor(final Class<?> aClass , int max, final Class<?>... classes) {
        for (Constructor<?> constructor : aClass.getConstructors()) {
            if (constructor.getParameterTypes().length <= max && Arrays.stream(constructor.getParameterTypes()).anyMatch(aClass1 -> aClass1 == classes[0])) return constructor;
        }
        return null;
    }

    public static String tocolor(String tocolor) {
        return ChatColor.translateAlternateColorCodes('&' , tocolor);
    }
}
