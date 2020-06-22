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
package ak.znetwork.znpcservers.npc;

import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * NPC API
 *
 * @author ZNetwork
 */
public class NPC {

    protected Object entityPlayer;
    protected Object entityPlayerArray;

    protected Object enumPlayerInfoAction;
    protected Constructor<?> getPacketPlayOutPlayerInfoConstructor;

    protected boolean hasGlow = false;

    protected int id;
    protected int entity_id;

    protected Location location;

    protected List<UUID> viewers;

    /**
     * Init of the necessary functionalities for the npc
     *
     * @param id the npc id
     * @param location the location for the npc
     */
    public NPC(final int id,final Location location) {
        this.viewers = new ArrayList<>();

        this.id = id;
        this.location = location;

        try {
            Object nmsServer = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            Object nmsWorld = location.getWorld().getClass().getMethod("getHandle").invoke(location.getWorld());

            Class<?> playerInteractManager = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PlayerInteractManager");
            Constructor<?> getPlayerInteractManagerConstructor = playerInteractManager.getDeclaredConstructors()[0];

            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + getBukkitPackage() + ".EntityPlayer");
            Constructor<?> getPlayerConstructor = entityPlayerClass.getDeclaredConstructors()[0];

            GameProfile gameProfile = new GameProfile(Bukkit.getOfflinePlayer("Notch").getUniqueId() , "test");

            entityPlayer = getPlayerConstructor.newInstance(nmsServer , nmsWorld , gameProfile , getPlayerInteractManagerConstructor.newInstance(nmsWorld));
            entityPlayer.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(entityPlayer , location.getX() , location.getY(),
                    location.getZ() , location.getYaw() , location.getPitch());

            Class<?> enumPlayerInfoActionClass = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            enumPlayerInfoAction = enumPlayerInfoActionClass.getField("ADD_PLAYER").get(null);

            Class<?> packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PacketPlayOutPlayerInfo");
            getPacketPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass , Class.forName("[Lnet.minecraft.server." + getBukkitPackage() + ".EntityPlayer;"));

            entityPlayerArray = Array.newInstance(entityPlayerClass, 1);
            Array.set(entityPlayerArray, 0, entityPlayer);

            entity_id = (Integer) entityPlayerClass.getMethod("getId").invoke(entityPlayer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggle the npc glow
     */
    public void toggleGlow()  {
        hasGlow = !hasGlow;

        try {
            Object dataWatcherObject = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);

            Field glowing = entityPlayer.getClass().getField("glowing");
            glowing.set("glowing" , Boolean.TRUE);

            /*

             */
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    /**
     * Spawn the npc for player
     * @param player to show npc
     */
    public void spawn(final Player player) {
        try {
            Object packetPlayOutPlayerInfoConstructor = getPacketPlayOutPlayerInfoConstructor.newInstance(enumPlayerInfoAction , entityPlayerArray);

            sendPacket(player ,packetPlayOutPlayerInfoConstructor);

            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getDeclaredConstructor(Class.forName("net.minecraft.server." + getBukkitPackage() + ".EntityHuman"));

            Object entityPlayerPacketSpawn = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayer);

            sendPacket(player ,entityPlayerPacketSpawn);

            viewers.add(player.getUniqueId());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }


    /**
     * Delete npc for palyer
     *
     * @param player to delete npc
     */
    public void delete(final Player player) {
        try {
            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + getBukkitPackage() + ".PacketPlayOutEntityDestroy");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getConstructor(int[].class);

            Object entityPlayerArray = Array.newInstance(int.class, 1);
            Array.set(entityPlayerArray, 0, entity_id);

            sendPacket(player ,getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayerArray));

            viewers.remove(player.getUniqueId());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sends the packet to the receiver
     *
     * @param player receiver
     * @param object packet to send
     */
    protected final void sendPacket(final Player player , final Object object) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);

            Method sendPacket = playerConnection.getClass().getMethod("sendPacket" , Class.forName("net.minecraft.server." + getBukkitPackage() + ".Packet"));

            sendPacket.invoke(playerConnection , object);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets current bukkit version
     *
     * @return bukkit version name
     */
    protected final String getBukkitPackage() {
        return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    }

    /**
     * Obtain the id of the npc
     *
     * @return npc id
     */
    public int getId() {
        return id;
    }

    /**
     * Obtain the location of the npc
     *
     * @return npc location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Obtain current viewers list
     *
     * @return viewers list
     */
    public List<UUID> getViewers() {
        return viewers;
    }
}
