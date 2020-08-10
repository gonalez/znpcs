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
import ak.znetwork.znpcservers.cache.ClazzCache;
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

    protected final ServersNPC serversNPC;

    public Location location;
    public String[] lines;

    protected List<Object> entityArmorStands;

    protected List<UUID> viewers;

    protected Object nmsWorld;

    protected Method IChatBaseComponentMethod;

    protected Constructor<?> getArmorStandConstructor;
    protected Constructor<?> getPacketPlayOutEntityTeleportConstructor;
    protected Constructor<?> getPacketPlayOutEntityMetadataConstructor;
    protected Constructor<?> getPacketPlayOutEntityDestroyConstructor;
    protected Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;


    public Hologram(final ServersNPC serversNPC , final Location location , final String... lines) {
        this.serversNPC = serversNPC;
        this.viewers = new ArrayList<>();

        this.entityArmorStands = new ArrayList<>();

        this.location = location;
        this.lines = lines;

        try {
            getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_SPAWN_ENTITY_CLASS.aClass.getDeclaredConstructor(ClazzCache.ENTITY_LIVING_CLASS.aClass);
            getPacketPlayOutEntityTeleportConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS.aClass.getConstructor(ClazzCache.ENTITY_CLASS.aClass);
            getPacketPlayOutEntityMetadataConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_METADATA_CLASS.aClass.getConstructor(int.class , ClazzCache.DATA_WATCHER_CLASS.aClass , boolean.class);
            getPacketPlayOutEntityDestroyConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS.aClass.getConstructor(int[].class);

            IChatBaseComponentMethod = ClazzCache.I_CHAT_BASE_COMPONENT_CLASS.aClass.getDeclaredClasses()[0].getMethod("a", String.class);

            nmsWorld = ClazzCache.GET_HANDLE_METHOD.method.invoke(location.getWorld());

            getArmorStandConstructor = ClazzCache.ENTITY_ARMOR_STAND_CLASS.aClass.getDeclaredConstructor(ClazzCache.WORLD_CLASS.aClass , double.class , double.class , double.class);

            createHolos();
        } catch (Exception e) {
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
            } catch (Exception e) {
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
            } catch (Exception e) {
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
                Object armorStand = getArmorStandConstructor.newInstance(nmsWorld , location.getX() + 0.5, location.getY() + (y) , location.getZ() + 0.5);

                armorStand.getClass().getMethod("setCustomNameVisible" , boolean.class).invoke(armorStand , (lines[i]).length() >= 1);
                if (Utils.isVersionNewestThan(13)) armorStand.getClass().getMethod("setCustomName" , ClazzCache.I_CHAT_BASE_COMPONENT_CLASS.aClass).invoke(armorStand , getStringNewestVersion(null, lines[i]));
                else armorStand.getClass().getMethod("setCustomName" , String.class).invoke(armorStand , ChatColor.translateAlternateColorCodes('&' , lines[i]));

                armorStand.getClass().getMethod("setInvisible" , boolean.class).invoke(armorStand , true);

                entityArmorStands.add(armorStand);

                y+=0.3;
            }

            viewers.forEach(uuid -> spawn(Bukkit.getPlayer(uuid) , false));
        } catch (Exception e) {
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
                if (Utils.isVersionNewestThan(13)) armorStand.getClass().getMethod("setCustomName" , ClazzCache.I_CHAT_BASE_COMPONENT_CLASS.aClass).invoke(armorStand , getStringNewestVersion(player, lines[i]));
                else armorStand.getClass().getMethod("setCustomName" , String.class).invoke(armorStand , ChatColor.translateAlternateColorCodes('&' , (serversNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(player , lines[i]) : line)));

                int entity_id = (Integer) armorStand.getClass().getMethod("getId").invoke(armorStand);

                Object dataWatcherObject = armorStand.getClass().getMethod("getDataWatcher").invoke(armorStand);

                ReflectionUtils.sendPacket(player , getPacketPlayOutEntityMetadataConstructor.newInstance(entity_id , dataWatcherObject , true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return
     */
    public Object getStringNewestVersion(final Player player, String text) {
        text = Utils.tocolor(text);
        try {
            return IChatBaseComponentMethod.invoke(null, "{\"text\": \"" + (serversNPC.isPlaceHolderSupport() && player != null ? PlaceholderUtils.getWithPlaceholders(player , text) : text) + "\"}");
        } catch (Exception e) {
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
            } catch (Exception e) {
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
                o.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(o , location.getX(), location.getY() + y,
                        location.getZ(), location.getYaw() , location.getPitch());

                y+=0.3;
            } catch (Exception e) {
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
