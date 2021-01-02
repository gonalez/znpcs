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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
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

    private final List<Object> entityArmorStands;

    private final HashSet<Player> viewers;

    private final Object nmsWorld;

    private final Method IChatBaseComponentMethod;

    public Hologram(final Location location , final String... lines) throws Exception {
        this.viewers = new HashSet<>();

        this.entityArmorStands = new ArrayList<>();

        this.location = location;
        this.lines = lines;

        IChatBaseComponentMethod = ClazzCache.ICHAT_BASE_COMPONENT_A_METHOD.method;

        nmsWorld = ClazzCache.GET_HANDLE_METHOD.method.invoke(location.getWorld());

        createHolos();
    }

    /**
     * Show npc for player
     *
     * @param player player to show hologram
     */
    public void spawn(final Player player , boolean add) {
        if (add) viewers.add(player);

        entityArmorStands.forEach(entityArmorStand -> {
            try {
                Object entityPlayerPacketSpawn = ClazzCache.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.constructor.newInstance(entityArmorStand);
                ReflectionUtils.sendPacket(player ,entityPlayerPacketSpawn);
            } catch (Exception e) {
                throw new RuntimeException("An exception occurred while trying to create hologram", e);
            }
        });
    }

    /**
     * Delete hologram for player
     *
     * @param player to delete npc
     */
    public void delete(final Player player , boolean remove) {
        if (remove) viewers.remove(player);

        entityArmorStands.forEach(entityArmorStand -> {
            try {
                Object entityArmorArray = Array.newInstance(int.class, 1);
                Array.set(entityArmorArray, 0, ClazzCache.GET_ID_METHOD.method.invoke(entityArmorStand));

                ReflectionUtils.sendPacket(player , ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.constructor.newInstance(entityArmorArray));
            } catch (Exception e) {
                throw new RuntimeException("An exception occurred while trying to delete hologram", e);
            }
        });
    }

    /**
     *
     */
    public void createHolos()  throws Exception {
        viewers.forEach(player -> delete(player, false));

        double y = 0;

        this.entityArmorStands.clear();

        for (int i = 0; i < Math.max(this.lines.length, this.lines.length); i++) {
            Object armorStand = ClazzCache.ARMOR_STAND_ENTITY_CONSTRUCTOR.constructor.newInstance(nmsWorld , location.getX() + 0.5, (location.getY() - 0.15) + (y) , location.getZ() + 0.5);

            ClazzCache.SET_CUSTOM_NAME_VISIBLE_METHOD.method.invoke(armorStand , (lines[i]).length() >= 1);
            if (Utils.isVersionNewestThan(13)) ClazzCache.SET_CUSTOM_NAME_NEW_METHOD.method.invoke(armorStand , getStringNewestVersion(null, lines[i]));
            else ClazzCache.SET_CUSTOM_NAME_OLD_METHOD.method.invoke(armorStand , ChatColor.translateAlternateColorCodes('&' , lines[i]));

            ClazzCache.SET_INVISIBLE_METHOD.method.invoke(armorStand , true);

            entityArmorStands.add(armorStand);

            y+=0.3;
        }

        viewers.forEach(player -> spawn(player, false));
    }

    /**
     *
     */
    public void updateNames(final Player player) throws Exception {
        for (int i = 0; i < Math.max(this.lines.length, this.lines.length); i++) {
            if (i >= entityArmorStands.size())
                continue;

            Object armorStand =  entityArmorStands.get(i);

            final String line = lines[i].replace(ServersNPC.getReplaceSymbol(), " ");

            if (Utils.isVersionNewestThan(13)) ClazzCache.SET_CUSTOM_NAME_NEW_METHOD.method.invoke(armorStand , getStringNewestVersion(player, lines[i]));
            else ClazzCache.SET_CUSTOM_NAME_OLD_METHOD.method.invoke(armorStand , ChatColor.translateAlternateColorCodes('&' , (ServersNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(player , lines[i]) : line)));

            int entity_id = (Integer) ClazzCache.GET_ID_METHOD.method.invoke(armorStand);

            Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.method.invoke(armorStand);

            ReflectionUtils.sendPacket(player , ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.constructor.newInstance(entity_id , dataWatcherObject , true));
        }
    }

    /**
     * Get real string for newer versions
     *
     * @return formated string
     */
    public Object getStringNewestVersion(final Player player, String text) {
        text = Utils.color(text);
        try {
            return IChatBaseComponentMethod.invoke(null, "{\"text\": \"" + (ServersNPC.isPlaceHolderSupport() && player != null ? PlaceholderUtils.getWithPlaceholders(player , text) : text.replace(ServersNPC.getReplaceSymbol(), " ")) + "\"}");
        } catch (Exception e) {
            throw new RuntimeException("An exception occurred while trying to get new line for hologram", e);
        }
    }

    /**
     * Update new loc
     */
    public void updateLoc() {
        entityArmorStands.forEach(o ->  {
            try {
                Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.constructor.newInstance(o);

                viewers.forEach(player -> ReflectionUtils.sendPacket(player, packet));
            } catch (Exception e) {
                throw new RuntimeException("An exception occurred while trying to update location for hologram", e);
            }
        });

    }

    /**
     * @param location
     */
    public void setLocation(Location location, double height) throws Exception{
        this.location = location.add(0 , height, 0);

        double y = 0;
        for (Object o : entityArmorStands) {
            ClazzCache.SET_LOCATION_METHOD.method.invoke(o, location.getX() + 0.5, (location.getY() - 0.15) + y,
                    location.getZ() + 0.5, location.getYaw() , location.getPitch());

            y+=0.3;
        }

        updateLoc();
    }

    public String getLinesFormatted() {
        return String.join(":" , lines);
    }
}
