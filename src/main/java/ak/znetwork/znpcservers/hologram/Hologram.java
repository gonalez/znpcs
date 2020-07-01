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
package ak.znetwork.znpcservers.hologram;

import ak.znetwork.znpcservers.utils.ReflectionUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Hologram {

    public final Location location;
    public final String[] lines;

    protected List<Object> entityArmorStands;

    protected List<UUID> viewers;

    public Hologram(final Location location , final String... lines) {
        this.viewers = new ArrayList<>();

        this.entityArmorStands = new ArrayList<>();

        this.location = location;
        this.lines = lines;

        Collections.reverse(Arrays.asList(lines));

        try {
            Object nmsWorld = location.getWorld().getClass().getMethod("getHandle").invoke(location.getWorld());

            Class<?> entityArmorStandClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityArmorStand");
            Constructor<?> getArmorStandConstructor = entityArmorStandClass.getDeclaredConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".World") , double.class , double.class , double.class);

            double y = 0;

            for (int i = 0; i < Math.max(this.lines.length, this.lines.length); i++) {
                Object armorStand = getArmorStandConstructor.newInstance(nmsWorld , location.getX() , location.getY() + (y) , location.getZ());

                armorStand.getClass().getMethod("setCustomNameVisible" , boolean.class).invoke(armorStand , (lines[i]).length() >= 1);
                armorStand.getClass().getMethod("setCustomName" , String.class).invoke(armorStand , ChatColor.translateAlternateColorCodes('&' , this.lines[i]));

                armorStand.getClass().getMethod("setInvisible" , boolean.class).invoke(armorStand , true);

                entityArmorStands.add(armorStand);

                y+=0.3;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show npc for player
     *
     * @param player player to show hologram
     */
    public void spawn(final Player player) {
        try {
            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntityLiving");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getDeclaredConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLiving"));

            entityArmorStands.forEach(entityArmorStand -> {
                try {
                    Object entityPlayerPacketSpawn = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityArmorStand);
                    ReflectionUtils.sendPacket(player ,entityPlayerPacketSpawn);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });

            viewers.add(player.getUniqueId());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete hologram for player
     *
     * @param player to delete npc
     */
    public void delete(final Player player) {
        try {
            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getConstructor(int[].class);

            entityArmorStands.forEach(entityArmorStand -> {
                try {
                    Object entityArmorArray = Array.newInstance(int.class, 1);
                    Array.set(entityArmorArray, 0, entityArmorStand.getClass().getMethod("getId").invoke(entityArmorStand));

                    ReflectionUtils.sendPacket(player , getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityArmorArray));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });

            viewers.remove(player.getUniqueId());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
