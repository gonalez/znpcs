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
import ak.znetwork.znpcservers.utils.Utils;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
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
    protected boolean hasMirror = false;

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

    protected Class<?> ItemStack ;
    protected Class<?> craftItemStack;

    protected Class<?> packetPlayOutEntityEquipment;
    protected Class<?> enumItemSlot;

    protected Class<?> packetPlayOutEntityDestroy;
    protected Constructor<?> getPacketPlayOutEntityDestroyConstructor;

    protected Class<?> packetPlayOutEntityHeadRotation;
    protected Constructor<?> getPacketPlayOutEntityHeadRotationConstructor;

    protected Class<?> packetPlayOutEntityLook;
    protected Constructor<?> getPacketPlayOutEntityLookConstructor;

    protected Class<?> packetPlayOutEntityTeleport;
    protected Constructor<?> getPacketPlayOutEntityTeleportConstructor;

    protected Class<?> packetPlayOutNamedEntitySpawn;
    protected Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;

    protected Class<?> packetPlayOutPlayerInfoClass;
    protected Class<?> enumPlayerInfoActionClass;

    protected Class<?> packetPlayOutEntityMetadata;
    protected Constructor<?> getPacketPlayOutEntityMetadataConstructor;

    protected Object getDataWatcher;
    protected Class<?> dataWatcherObject;
    protected Constructor<?> dataWatcherObjectConstructor;
    protected Object dataWatcherRegistryEnum;

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

            ItemStack = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".ItemStack");
            craftItemStack = Class.forName("org.bukkit.craftbukkit." + ReflectionUtils.getBukkitPackage() + ".inventory.CraftItemStack");

            packetPlayOutEntityDestroy = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityDestroy");
            getPacketPlayOutEntityDestroyConstructor = packetPlayOutEntityDestroy.getConstructor(int[].class);;

            packetPlayOutNamedEntitySpawn = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutNamedEntitySpawn");
            getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutNamedEntitySpawn.getDeclaredConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityHuman"));

            packetPlayOutEntityMetadata = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityMetadata");
            getPacketPlayOutEntityMetadataConstructor = packetPlayOutEntityMetadata.getConstructor(int.class , Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcher") , boolean.class);

            packetPlayOutEntityEquipment = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityEquipment");
            if (Utils.isVersionNewestThan(9))
                enumItemSlot = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EnumItemSlot");

            packetPlayOutPlayerInfoClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo");

            enumPlayerInfoActionClass = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction");

            packetPlayOutEntityTeleport = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityTeleport");
            getPacketPlayOutEntityTeleportConstructor = packetPlayOutEntityTeleport.getConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity"));

            packetPlayOutEntityHeadRotation = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntityHeadRotation");
            getPacketPlayOutEntityHeadRotationConstructor = packetPlayOutEntityHeadRotation.getConstructor(Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".Entity") , byte.class);

            packetPlayOutEntityLook = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutEntity$PacketPlayOutEntityLook");
            getPacketPlayOutEntityLookConstructor = packetPlayOutEntityLook.getConstructor(int.class , byte.class , byte.class , boolean.class);

            gameProfile = new GameProfile(UUID.randomUUID() , "znpc_" + getId());
            gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

            entityPlayer = getPlayerConstructor.newInstance(nmsServer , nmsWorld , gameProfile , getPlayerInteractManagerConstructor.newInstance(nmsWorld));

            entityPlayer.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(entityPlayer , location.getX() + 0.5, (location.getBlock().getType().name().toUpperCase().contains("STEP") ? location.getY() + 0.5: location.getY()),
                    location.getZ() + 0.5, location.getYaw() , location.getPitch());

            getDataWatcher = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);

            if (Utils.isVersionNewestThan(9)) {
                dataWatcherObject = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherObject");
                dataWatcherObjectConstructor = dataWatcherObject.getConstructor(int.class, Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherSerializer"));
                dataWatcherRegistryEnum = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherRegistry").getField("a").get(null);

                int version = Utils.getVersion();

                getDataWatcher.getClass().getMethod("set", dataWatcherObject, Object.class).invoke(getDataWatcher ,  dataWatcherObjectConstructor.newInstance((version == 12 || version == 13 ? 13 : (version == 14) ? 15 : 16), dataWatcherRegistryEnum) , (byte) 127);
            }
            else
                getDataWatcher.getClass().getMethod("watch", int.class, Object.class).invoke(getDataWatcher , 10 , (byte) 127);

            enumPlayerInfoAction = enumPlayerInfoActionClass.getField("ADD_PLAYER").get(null);

            getPacketPlayOutPlayerInfoConstructor = packetPlayOutPlayerInfoClass.getConstructor(enumPlayerInfoActionClass , Class.forName("[Lnet.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".EntityPlayer;"));

            entityPlayerArray = Array.newInstance(entityPlayerClass, 1);
            Array.set(entityPlayerArray, 0, entityPlayer);

            entity_id = (Integer) entityPlayerClass.getMethod("getId").invoke(entityPlayer);

            toggleName(true);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | InstantiationException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return get skin value
     */
    public String getSkin() {
        return skin;
    }

    /**
     * @return get skin signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param skin
     */
    public void setSkin(String skin) {
        this.skin = skin;
    }

    /**
     * @param signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
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
     * @return has same skin of players
     */
    public boolean isHasMirror() {
        return hasMirror;
    }

    /**
     * @param hasMirror set mirror mode
     */
    public void setHasMirror(boolean hasMirror) {
        this.hasMirror = hasMirror;
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
    public void toggleGlow(boolean fix)  {
        if (!Utils.isVersionNewestThan(9))
            return;

        if (fix)
        hasGlow = !hasGlow;

        try {
            getDataWatcher.getClass().getMethod("set", dataWatcherObject, Object.class).invoke(getDataWatcher ,  dataWatcherObjectConstructor.newInstance(0, dataWatcherRegistryEnum) , (hasGlow ? (byte) 0x40 : (byte) 0x0));

            Object dataWatcherObject = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);
            Object packete = getPacketPlayOutEntityMetadataConstructor.newInstance(entity_id , dataWatcherObject , true);

            viewers.forEach(uuid -> ReflectionUtils.sendPacket(Bukkit.getPlayer(uuid) , packete));
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
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
            Object stack = craftItemStack.getMethod("asNMSCopy" , ItemStack.class).invoke(craftItemStack , new ItemStack(material));

            Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;

            Object v16b = null;

            if (!Utils.isVersionNewestThan(9))
                getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutEntityEquipment.getConstructor(int.class , int.class , ItemStack);
            else   {
                if (Utils.isVersionNewestThan(16)) {
                    getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutEntityEquipment.getConstructor(int.class , List.class);

                    v16b = ReflectionUtils.getValue(packetPlayOutEntityEquipment.newInstance() , "b");
                }
                else
                    getPacketPlayOutNamedEntitySpawnConstructor = packetPlayOutEntityEquipment.getConstructor(int.class , enumItemSlot , ItemStack);
            }
            npcItemSlotMaterialHashMap.put(slot , material);

            Object packete;

            if (Utils.isVersionNewestThan(16)) {
                List<Pair<?, ?>> asd = (List<Pair<?, ?>>) v16b;
                asd.add(new Pair<>(enumItemSlot.getEnumConstants()[slot.getNewerv()]  , stack));

                packete = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entity_id , asd);
            } else  {
                packete = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entity_id , (Utils.isVersionNewestThan(9) ? enumItemSlot.getEnumConstants()[slot.getNewerv()] : slot.getId()), stack);
            }

            if (player == null) {
                getViewers().forEach(uuid -> {
                    ReflectionUtils.sendPacket(Bukkit.getPlayer(uuid), packete);
                });
            } else
                ReflectionUtils.sendPacket(player, packete);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Spawn the npc for player
     * @param player to show npc
     */
    public void spawn(final Player player) {
        try {
            toggleName(false);

            Object packetPlayOutPlayerInfoConstructor = getPacketPlayOutPlayerInfoConstructor.newInstance(enumPlayerInfoAction , entityPlayerArray);
            Object dataWatcherObject = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);

            if (hasMirror) {
                final GameProfile gameProfile = getGameProfileForPlayer(player);
                try {
                    Object gameProfileObj = entityPlayer.getClass().getMethod("getProfile").invoke(entityPlayer);

                    ReflectionUtils.setValue(gameProfileObj , "id" , UUID.randomUUID());
                    ReflectionUtils.setValue(gameProfileObj , "properties" , gameProfile.getProperties());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            ReflectionUtils.sendPacket(player ,packetPlayOutPlayerInfoConstructor);

            Object entityPlayerPacketSpawn = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entityPlayer);

            ReflectionUtils.sendPacket(player ,entityPlayerPacketSpawn);

            ReflectionUtils.sendPacket(player , getPacketPlayOutEntityMetadataConstructor.newInstance(entity_id , dataWatcherObject , true));

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
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void hideFromTablist(final Player player) {
        try {
            Object enumPlayerInfoAction = enumPlayerInfoActionClass.getField("REMOVE_PLAYER").get(null);

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
            Object entityPlayerArray = Array.newInstance(int.class, 1);
            Array.set(entityPlayerArray, 0, entity_id);

            ReflectionUtils.sendPacket(player , getPacketPlayOutEntityDestroyConstructor.newInstance(entityPlayerArray));

            if (removeViewer)
                viewers.remove(player.getUniqueId());

            hologram.delete(player , removeViewer);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
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
            ReflectionUtils.sendPacket(player , getPacketPlayOutEntityLookConstructor.newInstance(entity_id , (byte) ((direction.getYaw() %360.)*256/360) , (byte) ((direction.getPitch() %360.)*256/360) , false));
            ReflectionUtils.sendPacket(player , getPacketPlayOutEntityHeadRotationConstructor.newInstance(entityPlayer , (byte) ((direction.getYaw() %360.)*256/360)));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
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

    public void toggleMirror() {
        hasMirror = !hasMirror;
    }

    /**
     * Get clone of gameprofile for player
     *
     * @param player object
     * @return game profile for player
     */
    public GameProfile getGameProfileForPlayer(final Player player) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object gameProfileObj = craftPlayer.getClass().getMethod("getProfile").invoke(craftPlayer);

            GameProfile gameProfileClone = (GameProfile) gameProfileObj;
            GameProfile newProfile = new GameProfile(UUID.randomUUID(), "znpcs_" + getId());
            newProfile.getProperties().putAll(gameProfileClone.getProperties());

            return newProfile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Toggle npc name
     *
     * Hide/Show
     */
    @SuppressWarnings("unchecked")
    public void toggleName(boolean fix) {
        if (fix)
            hasToggleName = !hasToggleName;

        try {
            Object packetPlayOutScoreboardTeam = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayOutScoreboardTeam").getConstructor().newInstance();

            if (Utils.isVersionNewestThan(9)) {
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "i", (hasToggleName ? 0 : 1));
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", (Utils.isVersionNewestThan(13)) ? getHologram().getStringNewestVersion(gameProfile.getName()) : gameProfile.getName());
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", Utils.generateRandom());
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "j", 0);

                Collection<String> collection = Lists.newArrayList();
                collection.add(gameProfile.getName());

                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "h", collection);
            } else {
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "h", (hasToggleName ? 0 : 1));
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", gameProfile.getName());
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", Utils.generateRandom());
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "i", 1);

                Field f = packetPlayOutScoreboardTeam.getClass().getDeclaredField("g");
                f.setAccessible(true);

                Collection<String> collection = (Collection<String>) f.get(packetPlayOutScoreboardTeam);
                collection.add(gameProfile.getName());
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
            Object packete = getPacketPlayOutEntityTeleportConstructor.newInstance(entityPlayer);

            viewers.forEach(uuid -> {
                ReflectionUtils.sendPacket(Bukkit.getPlayer(uuid) ,  packete);
            });
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
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