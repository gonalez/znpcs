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

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.utils.PlaceholderUtils;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Hologram API
 *
 * @author ZNetwork
 *
 *
 * TODO
 * - CACHE MORE
 */
public class Hologram {

    public Location location;
    public String[] lines;

    protected List<Object> entityArmorStands;

    protected List<UUID> viewers;

    protected Object nmsWorld;
    protected Constructor<?> getArmorStandConstructor;

    protected final ServersNPC serversNPC;

    protected Class<?> IChatBaseComponent;
    protected Method IChatBaseComponentMethod;

    protected Class<?> packetPlayOutEntityTeleport;
    protected Constructor<?> getPacketPlayOutEntityTeleportConstructor;

    protected Class<?> packetPlayOutEntityMetadata;
    protected Constructor<?> getPacketPlayOutEntityMetadataConstructor;

    protected Class<?> packetPlayOutEntityDestroy;
    protected Constructor<?> getPacketPlayOutEntityDestroyConstructor;

    protected Class<?> packetPlayOutNamedEntitySpawn;
    protected Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;


    public Hologram(final ServersNPC serversNPC , final Location location , final String... lines) {
        this.serversNPC = serversNPC;
        this.viewers = new ArrayList<>();

        this.entityArmorStands = new ArrayList<>();

        this.location = location;
        this.lines = lines;

        Collections.reverse(Arrays.asList(lines));

        try {
            packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutSpawnEntityLiving");
            getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getDeclaredConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityLiving"));

            packetPlayOutEntityTeleport = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityTeleport");
            getPacketPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleport.getConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity"));

            packetPlayOutEntityMetadata = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityMetadata");
            getPacketPlayOutEntityMetadataConstructor = packetPlayOutEntityMetadata.getConstructor(int.class , Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcher") , boolean.class);

            packetPlayOutEntityDestroy = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy");
            getPacketPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroy.getConstructor(int[].class);


            IChatBaseComponent =  Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".IChatBaseComponent");
            IChatBaseComponentMethod = IChatBaseComponent.getDeclaredClasses()[0].getMethod("a", String.class);

            nmsWorld = location.getWorld().getClass().getMethod("getHandle").invoke(location.getWorld());

            Class<?> entityArmorStandClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityArmorStand");
            getArmorStandConstructor = entityArmorStandClass.getDeclaredConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".World") , double.class , double.class , double.class);

            createHolos();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show npc for player
     *
     * @param player player to show hologram
     */
    public void spawn(final Player player , boolean add) {
        entityArmorStands.forEach(entityArmorStand -> {
            try {
                Object entityPlayerPacketSpawn = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityArmorStand);
                ReflectionUtils.sendPacket(player ,entityPlayerPacketSpawn);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        if (add)
            viewers.add(player.getUniqueId());
    }

    /**
     * Delete hologram for player
     *
     * @param player to delete npc
     */
    public void delete(final Player player , boolean remove) {
        entityArmorStands.forEach(entityArmorStand -> {
            try {
                Object entityArmorArray = Array.newInstance(int.class, 1);
                Array.set(entityArmorArray, 0, entityArmorStand.getClass().getMethod("getId").invoke(entityArmorStand));

                ReflectionUtils.sendPacket(player , getPacketPlayOutEntityDestroyConstructor.newInstance(entityArmorArray));
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });

        if (remove)
            viewers.remove(player.getUniqueId());
    }

    /**
     *
     */
    public void createHolos() {
        viewers.forEach(uuid -> delete(Bukkit.getPlayer(uuid) , false));

        double y = 0;
        try {
            this.entityArmorStands.clear();

            for (int i = 0; i < Math.max(this.lines.length, this.lines.length); i++) {
                Object armorStand = getArmorStandConstructor.newInstance(nmsWorld , location.getX() , location.getY() + (y) , location.getZ());

                armorStand.getClass().getMethod("setCustomNameVisible" , boolean.class).invoke(armorStand , (lines[i]).length() >= 1);
                if (Utils.isVersionNewestThan(13))
                    armorStand.getClass().getMethod("setCustomName" , IChatBaseComponent).invoke(armorStand , getStringNewestVersion(lines[i]));
                else
                    armorStand.getClass().getMethod("setCustomName" , String.class).invoke(armorStand , ChatColor.translateAlternateColorCodes('&' , lines[i]));

                armorStand.getClass().getMethod("setInvisible" , boolean.class).invoke(armorStand , true);

                entityArmorStands.add(armorStand);

                y+=0.3;
            }

            viewers.forEach(uuid -> spawn(Bukkit.getPlayer(uuid) , false));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void updateNames(final Player player) {
        for (int i = 0; i < Math.max(this.lines.length, this.lines.length); i++) {
            if (i >= entityArmorStands.size())
                continue;

            Object armorStand =  entityArmorStands.get(i);

            final String line = lines[i].replace("_" , " ");

            try {
                if (Utils.isVersionNewestThan(13))
                    armorStand.getClass().getMethod("setCustomName" , IChatBaseComponent).invoke(armorStand , getStringNewestVersion(line));
                else
                    armorStand.getClass().getMethod("setCustomName" , String.class).invoke(armorStand , ChatColor.translateAlternateColorCodes('&' , (serversNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(player , lines[i]) : line)));

                int entity_id = (Integer) armorStand.getClass().getMethod("getId").invoke(armorStand);

                Object dataWatcherObject = armorStand.getClass().getMethod("getDataWatcher").invoke(armorStand);

                ReflectionUtils.sendPacket(player , getPacketPlayOutEntityMetadataConstructor.newInstance(entity_id , dataWatcherObject , true));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    public Object getStringNewestVersion(String text) {
        try {
            return IChatBaseComponentMethod.invoke(null, "{\"text\": \"" + ChatColor.translateAlternateColorCodes('&' , text) + "\"}");
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update new loc
     */
    public void updateLoc() {
        entityArmorStands.forEach(o ->  {
            try {
                Object packete = getPacketPlayOutEntityTeleportConstructor.newInstance(o);

                viewers.forEach(uuid -> {
                    ReflectionUtils.sendPacket(Bukkit.getPlayer(uuid) ,packete);
                });
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * @param location
     */
    public void setLocation(Location location) {
        this.location = location;

        double y = 0;

        for (Object o : entityArmorStands) {
            try {
                o.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(o , location.getX() , location.getY() + y,
                        location.getZ() , location.getYaw() , location.getPitch());

                y+=0.3;
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        updateLoc();
    }

    public String getLinesFormated() {
        StringJoiner joiner = new StringJoiner(":");
        for(int i = 0; i < lines.length; i++) {
            joiner.add(lines[i]);
        }

        return joiner.toString();
    }
}
