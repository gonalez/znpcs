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
import ak.znetwork.znpcservers.cache.ClazzCache;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    protected final ServersNPC serversNPC;

    protected Object entityPlayerArray;

    protected Object enumPlayerInfoAction;

    protected boolean hasGlow = false;
    protected boolean hasToggleName = false;
    protected boolean hasToggleHolo = true;
    protected boolean hasLookAt = false;
    protected boolean hasMirror = false;

    protected int id;
    protected int entity_id;

    protected Location location;

    protected HashSet<Player> viewers;

    protected Hologram hologram;

    protected GameProfile gameProfile;

    protected Object packetPlayOutScoreboardTeam;

    protected NPCAction npcAction;
    protected String[] actions;

    protected HashMap<NPCItemSlot , Material> npcItemSlotMaterialHashMap;

    protected String skin,signature;

    protected String glowName;
    protected Object glowColor;

    protected Constructor<?> getPacketPlayOutEntityDestroyConstructor;
    protected Constructor<?> getPacketPlayOutEntityHeadRotationConstructor;
    protected Constructor<?> getPacketPlayOutEntityLookConstructor;
    protected Constructor<?> getPacketPlayOutEntityTeleportConstructor;
    protected Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;
    protected Constructor<?> getPacketPlayOutEntityMetadataConstructor;
    protected Constructor<?> getPacketPlayOutPlayerInfoConstructor;
    protected Constructor<?> dataWatcherObjectConstructor;
    protected Constructor<?> getPlayerConstructor;
    protected Constructor<?> getPlayerInteractManagerConstructor;

    protected Object dataWatcherRegistryEnum;

    protected Object nmsWorld;
    protected Object nmsServer;

    protected Object packetPlayOutPlayerInfoConstructor;

    protected Object znEntity;

    protected final boolean save;

    protected NPCType npcType;

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
    public NPC(final ServersNPC serversNPC , final int id, final String skin , final String signature , final Location location , NPCType npcType, NPCAction npcAction, Hologram hologram , boolean save) throws Exception {
        this.serversNPC = serversNPC;

        this.npcItemSlotMaterialHashMap = new HashMap<>();
        this.viewers = new HashSet<>();

        this.skin = skin;
        this.signature = signature;

        this.hologram = hologram;

        this.save = save;

        this.id = id;
        this.location = location;

        this.npcAction = npcAction;

        nmsServer = ClazzCache.GET_SERVER_METHOD.method.invoke(Bukkit.getServer());
        nmsWorld = ClazzCache.GET_HANDLE_METHOD.method.invoke(location.getWorld());

        getPlayerInteractManagerConstructor = ClazzCache.PLAYER_INTERACT_MANAGER_CLASS.aClass.getDeclaredConstructors()[0];
        getPlayerConstructor = ClazzCache.ENTITY_PLAYER_CLASS.aClass.getDeclaredConstructors()[0];

        getPacketPlayOutEntityDestroyConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CLASS.aClass.getConstructor(int[].class);;
        getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_NAMED_ENTITY_SPAWN_CLASS.aClass.getDeclaredConstructor(ClazzCache.ENTITY_HUMAN_CLASS.aClass);
        getPacketPlayOutEntityMetadataConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_METADATA_CLASS.aClass.getConstructor(int.class , ClazzCache.DATA_WATCHER_CLASS.aClass , boolean.class);
        getPacketPlayOutEntityTeleportConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CLASS.aClass.getConstructor(ClazzCache.ENTITY_CLASS.aClass);
        getPacketPlayOutEntityHeadRotationConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CLASS.aClass.getConstructor(ClazzCache.ENTITY_CLASS.aClass , byte.class);
        getPacketPlayOutEntityLookConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_LOOK_CLASS.aClass.getConstructor(int.class , byte.class , byte.class , boolean.class);
        getPacketPlayOutPlayerInfoConstructor = ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO.aClass.getConstructor(ClazzCache.ENUM_PLAYER_INFO_ACTION_CLASS.aClass , ClazzCache.ENTITY_PLAYER_ARRAY_CLASS.aClass);

        gameProfile = new GameProfile(UUID.randomUUID() , "znpc_" + getId());
        gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        enumPlayerInfoAction = ClazzCache.ENUM_PLAYER_INFO_ACTION_CLASS.aClass.getField("ADD_PLAYER").get(null);

        changeType(npcType);

        toggleName(true);
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
     * @return get actions
     */
    public String[] getActions() {
        return actions;
    }

    /**
     * Set actions
     *
     * @param action set
     */
    public void setAction(String[] action) {
        this.actions = action;
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
     * Set glow color
     *
     * @param glowColor set
     */
    public void setGlowColor(Object glowColor) {
        this.glowColor = glowColor;
    }

    /**
     * Set glow color name
     *
     * @param glowName set
     */
    public void setGlowName(String glowName) {
        this.glowName = glowName;
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
     * Get color for glow
     *
     * @return glow color
     */
    public String getGlowName() {
        return glowName;
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
     * @return
     */
    public boolean isSave() {
        return save;
    }

    /**
     * Toggle the npc glow
     */
    public void toggleGlow(final Player player, final String color, boolean fix) throws Exception {
        if (!Utils.isVersionNewestThan(9)) return;
        if (fix) hasGlow = !hasGlow;

        Object dataWatcherObject = znEntity.getClass().getMethod("getDataWatcher").invoke(znEntity);

        dataWatcherObject.getClass().getMethod("set", ClazzCache.DATA_WATCHER_OBJECT_CLASS.aClass, Object.class).invoke(dataWatcherObject , dataWatcherObjectConstructor.newInstance(0, dataWatcherRegistryEnum) , (hasGlow ? (byte) 0x40 : (byte) 0x0));

        Object packet = getPacketPlayOutEntityMetadataConstructor.newInstance(entity_id , dataWatcherObject , true);

        this.glowColor = getGlowColor(color);
        this.glowName = color;

        if (player != null) ReflectionUtils.sendPacket(player , packet);

        // Update glow color
        toggleName(false);
    }

    public Object getGlowColor(final String string) throws Exception {
        try {
            return ClazzCache.ENUM_CHAT_FORMAT_CLASS.aClass.getField(string.toUpperCase()).get(null);
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException("Couldn't locate color " + string);
        }
    }

    /**
     * Equip npc
     *
     * @param player receiver
     * @param slot item slot
     * @param material material
     */
    public void equip(final Player player , NPCItemSlot slot , Material material) throws Exception {
        Object stack = ClazzCache.CRAFT_ITEM_STACK_CLASS.aClass.getMethod("asNMSCopy" , ItemStack.class).invoke(ClazzCache.CRAFT_ITEM_STACK_CLASS.aClass , new ItemStack(material));

        Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;

        Object v16b = null;

        if (!Utils.isVersionNewestThan(9))
            getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.getConstructor(int.class , int.class , ClazzCache.ITEM_STACK_CLASS.aClass);
        else{
            if (Utils.isVersionNewestThan(16)) {
                getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.getConstructor(int.class , List.class);

                v16b = ReflectionUtils.getValue(ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.newInstance() , "b");
            }
            else
                getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.getConstructor(int.class , ClazzCache.ENUM_ITEM_SLOT_CLASS.aClass , ClazzCache.ITEM_STACK_CLASS.aClass);
        }
        npcItemSlotMaterialHashMap.put(slot , material);

        Object packete;
        if (Utils.isVersionNewestThan(16)) {
            List<Pair<?, ?>> asd = (List<Pair<?, ?>>) v16b;
            asd.add(new Pair<>(ClazzCache.ENUM_ITEM_SLOT_CLASS.aClass.getEnumConstants()[slot.getNewerv()]  , stack));

            packete = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entity_id , asd);
        } else  {
            packete = getPacketPlayOutNamedEntitySpawnConstructor.newInstance(entity_id , (Utils.isVersionNewestThan(9) ? ClazzCache.ENUM_ITEM_SLOT_CLASS.aClass.getEnumConstants()[slot.getNewerv()] : slot.getId()), stack);
        }

        if (player == null) getViewers().forEach(player1 -> ReflectionUtils.sendPacket(player1, packete));
        else ReflectionUtils.sendPacket(player, packete);
    }

    /**
     * Update new skin for new players
     *
     * @param skinFetch value
     */
    public void updateSkin(final SkinFetch skinFetch) throws Exception {
        setSkin(skinFetch.value);
        setSignature(skinFetch.signature);

        final GameProfile gameProfile = new GameProfile(UUID.randomUUID() , "znpc_" + getId());
        gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        Object gameProfileObj = znEntity.getClass().getMethod("getProfile").invoke(znEntity);

        ReflectionUtils.setValue(gameProfileObj , "id" , UUID.randomUUID());
        ReflectionUtils.setValue(gameProfileObj , "properties" , gameProfile.getProperties());

        final Iterator<Player> it = this.getViewers().iterator();

        while (it.hasNext())  {
            final Player player = it.next();

            this.delete(player , false);

            it.remove();
        }
    }

    public void fixSkin() throws Exception {
        Object dataWatcherObject = znEntity.getClass().getMethod("getDataWatcher").invoke(znEntity);
        if (Utils.isVersionNewestThan(9)) {
            dataWatcherObjectConstructor = ClazzCache.DATA_WATCHER_OBJECT_CLASS.aClass.getConstructor(int.class, Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherSerializer"));
            dataWatcherRegistryEnum = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".DataWatcherRegistry").getField("a").get(null);

            int version = Utils.getVersion();

            dataWatcherObject.getClass().getMethod("set", ClazzCache.DATA_WATCHER_OBJECT_CLASS.aClass, Object.class).invoke(dataWatcherObject ,  dataWatcherObjectConstructor.newInstance((version == 12 || version == 13 ? 13 : (version == 14) ? 15 : 16), dataWatcherRegistryEnum) , (byte) 127);
        } else dataWatcherObject.getClass().getMethod("watch", int.class, Object.class).invoke(dataWatcherObject , 10 , (byte) 127);
    }

    public void changeType(final NPCType npcType) throws Exception {
        Constructor<?> constructor = (npcType != NPCType.PLAYER ? (Utils.isVersionNewestThan(13) ? (Utils.getDefinedConstructor(npcType.aClass.aClass, 2, ClazzCache.ENTITY_TYPES_CLASS.aClass)) : npcType.aClass.aClass.getConstructor(ClazzCache.WORLD_CLASS.aClass)) : null);

        znEntity = (npcType == NPCType.PLAYER ? getPlayerConstructor.newInstance(nmsServer , nmsWorld , gameProfile , getPlayerInteractManagerConstructor.newInstance(nmsWorld)) : (Utils.isVersionNewestThan(13) ? (Arrays.stream(constructor.getParameterTypes()).anyMatch(aClass -> aClass == ClazzCache.ENTITY_TYPES_CLASS.aClass) ? constructor.newInstance(((Optional<?>) ClazzCache.ENTITY_TYPES_A_METHOD.method.invoke(ClazzCache.ENTITY_TYPES_CLASS.aClass, npcType.name.toLowerCase())).get(), nmsWorld) : constructor.newInstance(nmsWorld)) : constructor.newInstance(nmsWorld)));

        if (npcType == NPCType.PLAYER) {
            entityPlayerArray = Array.newInstance(ClazzCache.ENTITY_PLAYER_CLASS.aClass, 1);
            Array.set(entityPlayerArray, 0, znEntity);

            packetPlayOutPlayerInfoConstructor = getPacketPlayOutPlayerInfoConstructor.newInstance(enumPlayerInfoAction , entityPlayerArray);

            // Fix second layer skin for entity player
            fixSkin();
        }

        this.npcType = npcType;

        // Update new type for viewers
        final Iterator<Player> it = this.getViewers().iterator();

        while (it.hasNext())  {
            final Player player = it.next();

            this.delete(player , false);

            it.remove();
        }

        // Update new id
        entity_id = (Integer) ClazzCache.ENTITY_PLAYER_CLASS.aClass.getMethod("getId").invoke(znEntity);
    }

    /**
     * Spawn the npc for player
     * @param player to show npc
     */
    public void spawn(final Player player) throws Exception {
        toggleName( false);

        if (npcType == NPCType.PLAYER && isHasMirror()) {
            final GameProfile gameProfile = getGameProfileForPlayer(player);

            Object gameProfileObj = znEntity.getClass().getMethod("getProfile").invoke(znEntity);

            ReflectionUtils.setValue(gameProfileObj , "id" , UUID.randomUUID());
            ReflectionUtils.setValue(gameProfileObj , "properties" , gameProfile.getProperties());
        }

        // Update location
        setLocation(this.location);

        if (npcType == NPCType.PLAYER) ReflectionUtils.sendPacket(player ,packetPlayOutPlayerInfoConstructor);

        ReflectionUtils.sendPacket(player, (npcType == NPCType.PLAYER ? getPacketPlayOutNamedEntitySpawnConstructor.newInstance(znEntity) : ((npcType.id < 0 ? npcType.constructor.newInstance(znEntity) : npcType.constructor.newInstance(znEntity, npcType.id)))));

        if (npcType == NPCType.PLAYER) {
            Object dataWatcherObject = znEntity.getClass().getMethod("getDataWatcher").invoke(znEntity);

            ReflectionUtils.sendPacket(player , getPacketPlayOutEntityMetadataConstructor.newInstance(entity_id , dataWatcherObject , true));
        }

        viewers.add(player);

        if (packetPlayOutScoreboardTeam != null) ReflectionUtils.sendPacket(player , packetPlayOutScoreboardTeam);
        if (hasToggleHolo) hologram.spawn(player , true);
        if (hasGlow) toggleGlow(player , glowName , false);

        // Update entity id
        entity_id = (Integer) ClazzCache.ENTITY_CLASS.aClass.getMethod("getId").invoke(znEntity);

        // Fix rotation
        lookAt(player , location.clone() , true);

        for (final Map.Entry<NPCItemSlot, Material> test : npcItemSlotMaterialHashMap.entrySet()) equip(player , test.getKey() , test.getValue());

        if (npcType == NPCType.PLAYER) {
            this.serversNPC.getExecutor().execute(() -> {
                try {
                    hideFromTablist(player);
                } catch (Exception e) {
                    throw new RuntimeException("An exception occurred while trying to hide npc" + getId(), e);
                }
            });
        }
    }

    public void hideFromTablist(final Player player) throws Exception {
        Object enumPlayerInfoAction = ClazzCache.ENUM_PLAYER_INFO_ACTION_CLASS.aClass.getField("REMOVE_PLAYER").get(null);

        Constructor<?> getPacketPlayOutPlayerInfoConstructor = ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO.aClass.getConstructor(ClazzCache.ENUM_PLAYER_INFO_ACTION_CLASS.aClass , ClazzCache.ENTITY_PLAYER_ARRAY_CLASS.aClass);

        Object packetPlayOutPlayerInfoConstructor = getPacketPlayOutPlayerInfoConstructor.newInstance(enumPlayerInfoAction , entityPlayerArray);

        ReflectionUtils.sendPacket(player , packetPlayOutPlayerInfoConstructor);
    }

    /**
     * Delete npc for player
     *
     * @param player to delete npc
     */
    public void delete(final Player player , boolean removeViewer) throws Exception {
        Object entityPlayerArray = Array.newInstance(int.class, 1);
        Array.set(entityPlayerArray, 0, entity_id);

        ReflectionUtils.sendPacket(player , getPacketPlayOutEntityDestroyConstructor.newInstance(entityPlayerArray));

        if (removeViewer) viewers.remove(player);

        hologram.delete(player , removeViewer);
    }

    /**
     * Makes the npc look at the location
     *
     * @param player receiver
     * @param location look at
     */
    public void lookAt(final Player player , final Location location , final boolean fix) throws Exception {
        Location direction = (fix ? this.location : this.location.clone().setDirection(location.subtract(this.location.clone()).toVector()));

        if (!fix) ReflectionUtils.sendPacket(player , getPacketPlayOutEntityLookConstructor.newInstance(entity_id , (byte) (direction.getYaw() % (!direction.equals(this.location) ? 360 : 0) * 256/360) ,  (byte) (direction.getPitch() % (!direction.equals(this.location) ? 360. : 0) * 256/360) , false));
        ReflectionUtils.sendPacket(player , getPacketPlayOutEntityHeadRotationConstructor.newInstance(znEntity , (byte) (((direction.getYaw()) * 256.0F) / 360.0F)));
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

        if (!hasToggleHolo) viewers.forEach(player -> hologram.delete(player, true));
        else viewers.forEach(player -> hologram.spawn(player, true));
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
    public GameProfile getGameProfileForPlayer(final Player player) throws Exception {
        Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object gameProfileObj = craftPlayer.getClass().getMethod("getProfile").invoke(craftPlayer);

        GameProfile gameProfileClone = (GameProfile) gameProfileObj;
        GameProfile newProfile = new GameProfile(UUID.randomUUID(), "znpcs_" + getId());
        newProfile.getProperties().putAll(gameProfileClone.getProperties());

        return newProfile;
    }

    /**
     * Toggle npc name
     *
     * Hide/Show
     */
    @SuppressWarnings("unchecked")
    public void toggleName(boolean fix) throws Exception {
        if (fix)
            hasToggleName = !hasToggleName;

        Object packetPlayOutScoreboardTeam = ClazzCache.PACKET_PLAY_OUT_SCOREBOARD_TEAM.aClass.getConstructor().newInstance();

        if (Utils.isVersionNewestThan(9)) {
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "i", (hasToggleName ? 0 : 1));
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", (Utils.isVersionNewestThan(13)) ? getHologram().getStringNewestVersion(null, gameProfile.getName()) : gameProfile.getName());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", Utils.generateRandom());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "j", 0);

            if (hasGlow && glowColor != null) ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", glowColor);

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

        viewers.forEach(player -> ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam));
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
     * Obtain the entity type of the npc
     *
     * @return npc type
     */
    public NPCType getNpcType() {
        return npcType;
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
    public void updateLoc() throws Exception {
        Object packet = getPacketPlayOutEntityTeleportConstructor.newInstance(znEntity);

        viewers.forEach(player -> ReflectionUtils.sendPacket(player , packet));
    }

    /**
     * Set new location for npc
     *
     * @param location new
     */
    public void setLocation(Location location) throws Exception {
        this.location = new Location(location.getWorld(), location.getBlockX() + 0.5, (location.getBlock().getType().name().toUpperCase().contains("STEP") ? location.getY() + 0.5: location.getY()), location.getBlockZ() + 0.5, location.getYaw() , location.getPitch());

        znEntity.getClass().getMethod("setLocation" , double.class , double.class , double.class , float.class , float.class).invoke(znEntity , this.location.getX(),  this.location.getY(), this.location.getZ(), location.getYaw() , location.getPitch());

        hologram.setLocation(this.location.clone().subtract(0.5, 0 , 0.5), this.npcType.holoHeight);

        updateLoc();
    }

    /**
     * Obtain current viewers list
     *
     * @return viewers list
     */
    public HashSet<Player> getViewers() {
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