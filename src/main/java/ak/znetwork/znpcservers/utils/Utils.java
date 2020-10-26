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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

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

    public static boolean containsStep(final Location location) {
        return location.getBlock().getType().name().contains("STEP");
    }

    public static Constructor<?> getDefinedConstructor(final Class<?> aClass , int max, final Class<?>... classes) {
        for (Constructor<?> constructor : aClass.getConstructors()) {
            if (constructor.getParameterTypes().length <= max && Arrays.stream(constructor.getParameterTypes()).anyMatch(aClass1 -> aClass1 == classes[0])) return constructor;
        }
        return null;
    }

    public static List<Class<?>> getClasses(final String jarName, final Class<?> subType)  {
        List<Class<?>> classSet = new ArrayList<>();

        final File pluginFile = new File("plugins/" + jarName + ".jar");
        try {
            final URLClassLoader loader = URLClassLoader.newInstance(new URL[]{pluginFile.toURI().toURL()}, subType.getClassLoader());;

            final JarFile jarFile = new JarFile(pluginFile);

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();

                if (name.endsWith(".class")) {
                    final String last = name.replace("/" , ".").replace(".class" , "");

                    final Class<?> aClass = loader.loadClass(last);

                    if (aClass != null && aClass.getSuperclass() != null && aClass.getSuperclass().getName().equalsIgnoreCase(subType.getName())) {
                        classSet.add(aClass);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.WARNING , "er" , e);
        }
        return classSet;
    }

    public static String color(String tocolor) {
        return ChatColor.translateAlternateColorCodes('&' , tocolor);
    }
}
