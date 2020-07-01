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

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.*;
import java.util.*;

/**
 * NPC API
 *
 * @author ZNetwork
 *
 *
 * TODO
 * - CACHE MORE
 */
public class NPC {

    protected Object entityPlayer;
    protected Object entityPlayerArray;

    protected Object enumPlayerInfoAction;
    protected Constructor<?> getPacketPlayOutPlayerInfoConstructor;

    protected boolean hasGlow = false;
    protected boolean hasToggleName = false;
    protected boolean hasToggleHolo = true;
    protected boolean hasLookAt = false;

    protected int id;
    protected int entity_id;

    protected Location location;

    protected List<UUID> viewers;

    protected Hologram hologram;

    protected GameProfile gameProfile;

    protected Object packetPlayOutScoreboardTeam;

    protected NPCAction npcAction;
    protected String action;

    protected HashMap<NPCItemSlot , Material> npcItemSlotMaterialHashMap;

    protected String skin,signature;

    protected final ServersNPC serversNPC;

    /**
     * Init of the necessary functionalities for the npc
     *
     * @param id the npc id
     * @param skin the skin value
     * @param signature the skin signature
     * @param location the location for the npc
     * @param npcAction npc action type
     * @param hologram
     */
    public NPC(final ServersNPC serversNPC , final int id, final String skin , final String signature , final Location location , NPCAction npcAction, Hologram hologram) {
        this.serversNPC = serversNPC;

        this.npcItemSlotMaterialHashMap = new HashMap<>();
        this.viewers = new ArrayList<>();

        this.skin = skin;
        this.signature = signature;

        this.hologram = hologram;

        this.id = id;
        this.location = location;

        this.npcAction = npcAction;

        try {
            Object nmsServer = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            Object nmsWorld = location.getWorld().getClass().getMethod("getHandle").invoke(location.getWorld());

            Class<?> playerInteractManager = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PlayerInteractManager");
            Constructor<?> getPlayerInteractManagerConstructor = playerInteractManager.getDeclaredConstructors()[0];

            Class<?> entityPlayerClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer");
            Constructor<?> getPlayerConstructor = entityPlayerClass.getDeclaredConstructors()[0];

            gameProfile = new GameProfile(UUID.randomUUID() , "znpc_" + getId());
            gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

            entityPlayer = getPlayerConstructor.newInstance(nmsServer , nmsWorld , gameProfile , getPlayerInteractManagerConstructor.newInstance(nmsWorld));
            entityPlayer.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(entityPlayer , location.getX() , location.getY(),
                    location.getZ() , location.getYaw() , location.getPitch());

            Object getDataWatcher = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);

            //getDataWatcher.getClass().getMethod("watch", int.class, Object.class).invoke(getDataWatcher , 10 , (byte) 127);

            Class<?> enumPlayerInfoActionClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            enumPlayerInfoAction = enumPlayerInfoActionClass.getField("ADD_PLAYER").get(null);

            Class<?> packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo");
            getPacketPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass , Class.forName("[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;"));

            entityPlayerArray = Array.newInstance(entityPlayerClass, 1);
            Array.set(entityPlayerArray, 0, entityPlayer);

            entity_id = (Integer) entityPlayerClass.getMethod("getId").invoke(entityPlayer);

            toggleName();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return get action
     */
    public String getAction() {
        return action;
    }

    /**
     * Set action
     *
     * @param action set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Set action type
     *
     * @param npcAction set
     */
    public void setNpcAction(NPCAction npcAction) {
        this.npcAction = npcAction;
    }

    /**
     * @return has holo
     */
    public boolean isHasToggleHolo() {
        return hasToggleHolo;
    }

    /**
     * @return has glow visible
     */
    public boolean isHasGlow() {
        return hasGlow;
    }

    /**
     * @return has bukkit name visibile
     */
    public boolean isHasToggleName() {
        return hasToggleName;
    }

    /**
     * Set glow visibility
     *
     * @param hasGlow set
     */
    public void setHasGlow(boolean hasGlow) {
        this.hasGlow = hasGlow;
    }

    /**
     * Set holo visibility
     *
     * @param hasToggleHolo set
     */
    public void setHasToggleHolo(boolean hasToggleHolo) {
        this.hasToggleHolo = hasToggleHolo;
    }

    /**
     * Npc look at location toggle
     *
     * @param hasLookAt set
     */
    public void setHasLookAt(boolean hasLookAt) {
        this.hasLookAt = hasLookAt;
    }

    /**
     * Toggle name for npc
     *
     * @param hasToggleName set
     */
    public void setHasToggleName(boolean hasToggleName) {
        this.hasToggleName = hasToggleName;
    }

    /**

     * Get action type
     *
     * @return get npc action
     */
    public NPCAction getNpcAction() {
        return npcAction;
    }

    /**
     * @return bukkit entity id
     */
    public int getEntity_id() {
        return entity_id;
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
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    /**
     * Equip npc
     *
     * @param player receiver
     * @param slot item slot
     * @param material material
     */
    public void equip(final Player player , NPCItemSlot slot , Material material) {
        try {
            Class<?> ItemStack = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ItemStack");
            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".inventory.CraftItemStack");

            Object stack = craftItemStack.getMethod("asNMSCopy" , ItemStack.class).invoke(craftItemStack , new ItemStack(material));

            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityEquipment");
            Class<?> enumItemSlot = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumItemSlot");

            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;

            if (ReflectionUtils.getFriendlyBukkitPackage().startsWith("8"))
                getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getConstructor(int.class , int.class , ItemStack);
            else
                getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getConstructor(int.class , enumItemSlot , ItemStack);

            npcItemSlotMaterialHashMap.put(slot , material);

            if (player == null) {
                getViewers().forEach(uuid -> {
                    try {
                        ReflectionUtils.sendPacket(Bukkit.getPlayer(uuid) ,getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entity_id , (!ReflectionUtils.getFriendlyBukkitPackage().startsWith("8") ? enumItemSlot.getEnumConstants()[slot.getNewerv()] : slot.getId()), stack));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
            } else
                ReflectionUtils.sendPacket(player ,getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entity_id , (!ReflectionUtils.getFriendlyBukkitPackage().startsWith("8") ? enumItemSlot.getEnumConstants()[slot.getNewerv()] : slot.getId()), stack));
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
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

            ReflectionUtils.sendPacket(player ,packetPlayOutPlayerInfoConstructor);

            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getDeclaredConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHuman"));

            Object entityPlayerPacketSpawn = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayer);

            ReflectionUtils.sendPacket(player ,entityPlayerPacketSpawn);

            viewers.add(player.getUniqueId());

            if (packetPlayOutScoreboardTeam != null)
                ReflectionUtils.sendPacket(player , packetPlayOutScoreboardTeam);

            if (hasToggleHolo)
                hologram.spawn(player , true);

            for (final Map.Entry<NPCItemSlot, Material> test : npcItemSlotMaterialHashMap.entrySet())
                equip(player , test.getKey() , test.getValue());

            new BukkitRunnable() {

                @Override
                public void run() {
                    hideFromTablist(player);
                }
            }.runTaskLater(serversNPC , 20L);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void hideFromTablist(final Player player) {
        try {
            Class<?> enumPlayerInfoActionClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");
            Object enumPlayerInfoAction = enumPlayerInfoActionClass.getField("REMOVE_PLAYER").get(null);

            Class<?> packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo");
            Constructor<?> getPacketPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass , Class.forName("[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;"));

            Object packetPlayOutPlayerInfoConstructor = getPacketPlayOutPlayerInfoConstructor.newInstance(enumPlayerInfoAction , entityPlayerArray);

            ReflectionUtils.sendPacket(player ,packetPlayOutPlayerInfoConstructor);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }


    /**
     * Delete npc for player
     *
     * @param player to delete npc
     */
    public void delete(final Player player , boolean removeViewer) {
        try {
            Class<?> packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getConstructor(int[].class);

            Object entityPlayerArray = Array.newInstance(int.class, 1);
            Array.set(entityPlayerArray, 0, entity_id);

            ReflectionUtils.sendPacket(player ,getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayerArray));

            if (removeViewer)
                viewers.remove(player.getUniqueId());

            hologram.delete(player , removeViewer);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes the npc look at the location
     *
     * @param player receiver
     * @param location look at
     */
    public void lookAt(final Player player , final Location location) {
        final Location direction = this.location.setDirection(location.subtract(this.location).toVector());

        try {
            Class<?> packetPlayOutEntityHeadRotation = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityHeadRotation");
            Constructor<?> getPacketPlayOutEntityHeadRotationConstructor = packetPlayOutEntityHeadRotation.getConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity") , byte.class);

            Class<?> packetPlayOutEntityLook = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntity$PacketPlayOutEntityLook");
            Constructor<?> getPacketPlayOutEntityLookConstructor = packetPlayOutEntityLook.getConstructor(int.class , byte.class , byte.class , boolean.class);

            ReflectionUtils.sendPacket(player , getPacketPlayOutEntityLookConstructor.newInstance(entity_id , (byte) ((direction.getYaw() %360.)*256/360) , (byte) ((direction.getPitch() %360.)*256/360) , false));
            ReflectionUtils.sendPacket(player , getPacketPlayOutEntityHeadRotationConstructor.newInstance(entityPlayer , (byte) ((direction.getYaw() %360.)*256/360)));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * Toggle (hide / show) look
     */
    public void toggleLookAt() {
        hasLookAt = !hasLookAt;
    }

    /**
     * Toggle npc holo
     */
    public void toggleHolo() {
        hasToggleHolo = !hasToggleHolo;

        if (!hasToggleHolo)
            viewers.forEach(uuid -> hologram.delete(Bukkit.getPlayer(uuid) , true));
        else
            viewers.forEach(uuid -> hologram.spawn(Bukkit.getPlayer(uuid) , true));
    }

    /**
     * Toggle npc name
     *
     * Hide/Show
     */
    @SuppressWarnings("unchecked")
    public void toggleName() {
        hasToggleName = !hasToggleName;

        try {
            Object packetPlayOutScoreboardTeam = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutScoreboardTeam").getConstructor().newInstance();


            if (ReflectionUtils.getFriendlyBukkitPackage().startsWith("8")) {
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "h", (hasToggleName ? 0 : 1));
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", gameProfile.getName());
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", gameProfile.getName());
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "i", 1);

                Field f = packetPlayOutScoreboardTeam.getClass().getDeclaredField("g");
                f.setAccessible(true);

                Collection<String> collection = (Collection<String>) f.get(packetPlayOutScoreboardTeam);
                collection.add(gameProfile.getName());
            } else {
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "i", (hasToggleName ? 0 : 1));
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", getHologram().getStringNewestVersion(gameProfile.getName()));
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", gameProfile.getName());
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "j", 0);

                Collection<String> collection = Lists.newArrayList();
                collection.add(gameProfile.getName());

                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "h", collection);
            }

            this.packetPlayOutScoreboardTeam = packetPlayOutScoreboardTeam;
            viewers.forEach(uuid -> ReflectionUtils.sendPacket(Bukkit.getPlayer(uuid) , packetPlayOutScoreboardTeam));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * @return npc item slots with mat name
     */
    public HashMap<NPCItemSlot, Material> getNpcItemSlotMaterialHashMap() {
        return npcItemSlotMaterialHashMap;
    }

    /**
     * Get hologram for npc
     *
     * @return holo
     */
    public Hologram getHologram() {
        return hologram;
    }

    /**
     * Update new loc
     */
    public void updateLoc() {
        try {
            Class<?> packetPlayOutEntityTeleport = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityTeleport");
            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutEntityTeleport.getConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity"));

            viewers.forEach(uuid -> {
                try {
                    ReflectionUtils.sendPacket(Bukkit.getPlayer(uuid) , getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayer));
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set new location for npc
     *
     * @param location new
     */
    public void setLocation(Location location) {
        this.location = location;

        try {
            entityPlayer.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(entityPlayer , location.getX() , location.getY(),
                    location.getZ() , location.getYaw() , location.getPitch());

            hologram.setLocation(location);

            updateLoc();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtain current viewers list
     *
     * @return viewers list
     */
    public List<UUID> getViewers() {
        return viewers;
    }

    public boolean isHasLookAt() {
        return hasLookAt;
    }

    /**
     * NPC ITEM SLOT
     *
     * Get slot by id
     */
    public enum NPCItemSlot  {
        HAND(0 , 0) , HELMET(4 , 5) , CHESTPLATE(3 , 4) , LEGGINGS(2 , 3) , BOOTS(1 ,  2);

        int id;
        int newerv;

        NPCItemSlot(int id , int newerv) {
            this.id = id;
            this.newerv = newerv;
        }

        public int getId() {
            return id;
        }

        public int getNewerv() {
            return newerv;
        }

        public static NPCItemSlot fromString(String text) {
            for (NPCItemSlot b : NPCItemSlot.values()) {
                if (b.name().toUpperCase().equalsIgnoreCase(text.toUpperCase())) {
                    return b;
                }
            }
            return null;
        }
    }
}
