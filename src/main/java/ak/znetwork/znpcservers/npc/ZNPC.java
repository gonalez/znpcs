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
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.types.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;

public class ZNPC {

    // Serialize
    @Expose
    private final int id;
    @Expose
    private boolean hasGlow = false;
    @Expose
    private boolean hasToggleName = false;
    @Expose
    private boolean hasToggleHolo = true;
    @Expose
    private boolean hasLookAt = false;
    @Expose
    private boolean hasMirror = false;
    @Expose
    private boolean isReversePath = false;

    @Expose
    private Location location;

    @Expose
    private List<String> actions;

    @Expose
    private final EnumMap<NPCItemSlot, Material> npcEquipments;

    @Expose
    private String skin, signature;

    @Expose
    private String glowName = "WHITE";

    @Expose
    private final boolean save;

    @Expose
    private NPCType npcType;

    @Expose
    private String lines;

    @Expose
    public Map<String, List> customizationMap;

    @Expose
    public String pathName;

    /*
    Serialize = false
     */
    private final ServersNPC serversNPC;

    private String worldName;

    private Object entityPlayerArray;
    private Object glowColor;
    private Object dataWatcherRegistryEnum;
    private Object packetPlayOutPlayerInfoConstructor;
    private Object znEntity;
    private Object packetPlayOutScoreboardTeam;

    private final Object nmsWorld;
    private final Object nmsServer;

    private final Object ADD_PLAYER;
    private final Object REMOVE_PLAYER;

    private final GameProfile gameProfile;

    private final Hologram hologram;

    private final HashSet<Player> viewers;

    private int entity_id;

    /*
    PATH
     */
    private ZNPCPathReader npcPath;

    private int currentEntryPath = 0;

    private Location currentPathLocation;

    private boolean reversePath = false;

    /**
     * Init of the necessary functionalities for the npc
     *
     * @param id        the npc id
     * @param skin      the skin value
     * @param signature the skin signature
     * @param location  the location for the npc
     */
    public ZNPC(final ServersNPC serversNPC, final int id, final String lines, final String skin, final String signature, final Location location, NPCType npcType, EnumMap<NPCItemSlot, Material> npcEquipments, boolean save) throws Exception {
        this.serversNPC = serversNPC;

        this.npcEquipments = npcEquipments;

        this.viewers = new HashSet<>();

        this.skin = skin;
        this.signature = signature;

        this.save = save;

        this.hologram = new Hologram(location, lines.split(":"));
        this.lines = hologram.getLinesFormatted();

        this.id = id;
        this.location = (location);

        this.actions = new ArrayList<>();

        this.customizationMap = new HashMap<>();

        this.pathName = "none";

        nmsServer = ClazzCache.GET_SERVER_METHOD.getCacheMethod().invoke(Bukkit.getServer());
        nmsWorld = ClazzCache.GET_HANDLE_METHOD.getCacheMethod().invoke(location.getWorld());

        gameProfile = new GameProfile(UUID.randomUUID(), "znpc_" + getId());
        gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        ADD_PLAYER = ClazzCache.ADD_PLAYER_FIELD.getCacheField().get(null);
        REMOVE_PLAYER = ClazzCache.REMOVE_PLAYER_FIELD.getCacheField().get(null);

        changeType(npcType);

        toggleName(true);
    }

    /**
     * Toggle the npc glow
     */
    public void toggleGlow(final Optional<Player> playerOptional, final String color, boolean fix) throws Exception {
        if (!Utils.isVersionNewestThan(9)) throw new UnsupportedOperationException("Version not supported");
        if (fix) hasGlow = !hasGlow;

        Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.getCacheMethod().invoke(znEntity);

        ClazzCache.SET_DATA_WATCHER_METHOD.getCacheMethod().invoke(dataWatcherObject, ClazzCache.DATA_WATCHER_OBJECT_CONSTRUCTOR.getCacheConstructor().newInstance(0, dataWatcherRegistryEnum), (hasGlow ? (byte) 0x40 : (byte) 0x0));

        Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.getCacheConstructor().newInstance(entity_id, dataWatcherObject, true);

        this.glowColor = getGlowColor(color);
        this.glowName = color;

        playerOptional.ifPresent(player -> ReflectionUtils.sendPacket(player, packet));
        // Update glow color
        toggleName(false);
    }

    public void handlePath() {
        if (npcPath == null) return;

        try {
            if (isReversePath) {
                if (currentEntryPath <= 0) reversePath = false;
                else if (currentEntryPath >= npcPath.getLocationList().size() - 1) reversePath = true;
            }

            currentPathLocation = npcPath.getLocationList().get(Math.min(npcPath.getLocationList().size() - 1, currentEntryPath));

            if (!reversePath) currentEntryPath++;
            else currentEntryPath--;

            updatePathLocation(npcPath, currentPathLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Update new location
     */
    public void updateLoc() throws Exception {
        Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.getCacheConstructor().newInstance(znEntity);

        viewers.forEach(player -> ReflectionUtils.sendPacket(player, packet));
    }

    /**
     * Set new location for npc
     *
     * @param location new
     */
    public void setLocation(Location location) throws Exception {
        this.location = new Location(location.getWorld(), location.getBlockX() + 0.5D, location.getY(), location.getBlockZ() + 0.5D, location.getYaw(), location.getPitch());

        ClazzCache.SET_LOCATION_METHOD.getCacheMethod().invoke(znEntity, this.location.getX(), this.location.getY(), this.location.getZ(), location.getYaw(), location.getPitch());

        if (hologram != null)
            hologram.setLocation(this.location.clone().subtract(0.5, 0, 0.5), this.npcType.getHoloHeight());

        lookAt(Optional.empty(), location, true);

        updateLoc();
    }

    /**
     * Equip npc
     *
     * @param player   receiver
     * @param slot     item slot
     * @param material material
     */
    public void equip(final Player player, NPCItemSlot slot, Material material) throws Exception {
        Object item = ClazzCache.AS_NMS_COPY_METHOD.getCacheMethod().invoke(ClazzCache.CRAFT_ITEM_STACK_CLASS.getCacheClass(), new ItemStack(material));

        Object equipPacket;
        if (!Utils.isVersionNewestThan(9)) {
            equipPacket = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD.getCacheConstructor().newInstance(entity_id, slot.getId(), item);
        } else {
            if (Utils.isVersionNewestThan(16)) {
                List<Pair<?, ?>> pairs = (List<Pair<?, ?>>) ReflectionUtils.getValue(ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.getCacheClass().newInstance(), "b");
                pairs.add(new Pair<>(ClazzCache.ENUM_ITEM_SLOT_CLASS.getCacheClass().getEnumConstants()[slot.getId() + 1], item));

                equipPacket = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEW.getCacheConstructor().newInstance(entity_id, pairs);
            } else {
                equipPacket = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD.getCacheConstructor().newInstance(entity_id, ClazzCache.ENUM_ITEM_SLOT_CLASS.getCacheClass().getEnumConstants()[slot.getId() + 1], item);
            }
        }
        npcEquipments.put(slot, material);

        if (player != null) ReflectionUtils.sendPacket(player, equipPacket);
        else viewers.forEach(player1 -> ReflectionUtils.sendPacket(player1, equipPacket));
    }

    /**
     * Update new skin for new players
     *
     * @param skinFetch value
     */
    public void changeSkin(final SkinFetch skinFetch) throws Exception {
        setSkin(skinFetch.getValue());
        setSignature(skinFetch.getSignature());

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "znpc_" + getId());
        gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        Object gameProfileObj = ClazzCache.GET_PROFILE_METHOD.getCacheMethod().invoke(znEntity);

        ReflectionUtils.setValue(gameProfileObj, "id", UUID.randomUUID());
        ReflectionUtils.setValue(gameProfileObj, "properties", gameProfile.getProperties());

        final Iterator<Player> it = this.getViewers().iterator();
        while (it.hasNext()) {
            final Player player = it.next();

            delete(player, false);

            it.remove();
        }
    }

    public void fixSkin() throws Exception {
        Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.getCacheMethod().invoke(znEntity);
        if (Utils.isVersionNewestThan(9)) {
            dataWatcherRegistryEnum = ClazzCache.DATA_WATCHER_REGISTER_ENUM_FIELD.getCacheField().get(null);

            int version = Utils.getVersion();

            ClazzCache.SET_DATA_WATCHER_METHOD.getCacheMethod().invoke(dataWatcherObject, ClazzCache.DATA_WATCHER_OBJECT_CONSTRUCTOR.getCacheConstructor().newInstance(version < 11 ? 10 : (version == 11 || version == 12 || version == 13 ? 13 : (version == 14) ? 15 : 16), dataWatcherRegistryEnum), (byte) 127);
        } else ClazzCache.WATCH_DATA_WATCHER_METHOD.getCacheMethod().invoke(dataWatcherObject, 10, (byte) 127);
    }

    public void changeType(final NPCType npcType) throws Exception {
        Constructor<?> constructor = (npcType != NPCType.PLAYER ? (Utils.isVersionNewestThan(13) ? npcType.getClazzCache().getCacheClass().getConstructor(npcType.getEntityType().getClass(), ClazzCache.WORLD_CLASS.getCacheClass()) : npcType.getClazzCache().getCacheClass().getConstructor(ClazzCache.WORLD_CLASS.getCacheClass())) : null);

        znEntity = (npcType == NPCType.PLAYER ? ClazzCache.PLAYER_CONSTRUCTOR.getCacheConstructor().newInstance(nmsServer, nmsWorld, gameProfile, ClazzCache.PLAYER_INTERACT_MANAGER_CONSTRUCTOR.getCacheConstructor().newInstance(nmsWorld)) : (Utils.isVersionNewestThan(13) ? constructor.newInstance(npcType.getEntityType(), nmsWorld) : constructor.newInstance(nmsWorld)));

        if (npcType == NPCType.PLAYER) {
            entityPlayerArray = Array.newInstance(ClazzCache.ENTITY_PLAYER_CLASS.getCacheClass(), 1);
            Array.set(entityPlayerArray, 0, znEntity);

            packetPlayOutPlayerInfoConstructor = ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.getCacheConstructor().newInstance(ADD_PLAYER, entityPlayerArray);

            // Fix second layer skin for entity player
            fixSkin();
        }

        this.npcType = npcType;

        setLocation(this.location);

        // Update new type for viewers
        final Iterator<Player> it = this.getViewers().iterator();

        while (it.hasNext()) {
            final Player player = it.next();

            this.delete(player, false);

            it.remove();
        }

        // Update new id
        entity_id = (Integer) ClazzCache.GET_ID_METHOD.getCacheMethod().invoke(znEntity);
    }

    /**
     * Spawn the npc for player
     *
     * @param player to show npc
     */
    public void spawn(final Player player) throws Exception {
        toggleName(false);

        if (npcType == NPCType.PLAYER && isHasMirror()) {
            final GameProfile gameProfile = getGameProfileForPlayer(player);

            Object gameProfileObj = ClazzCache.GET_PROFILE_METHOD.getCacheMethod().invoke(znEntity);

            ReflectionUtils.setValue(gameProfileObj, "id", UUID.randomUUID());
            ReflectionUtils.setValue(gameProfileObj, "properties", gameProfile.getProperties());
        }

        // Update location
        setLocation(this.location);

        if (npcType == NPCType.PLAYER) ReflectionUtils.sendPacket(player, packetPlayOutPlayerInfoConstructor);

        ReflectionUtils.sendPacket(player, (npcType == NPCType.PLAYER ? ClazzCache.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.getCacheConstructor().newInstance(znEntity) : ((npcType.getId() < 0 ? npcType.getConstructor().newInstance(znEntity) : npcType.getConstructor().newInstance(znEntity, npcType.getId())))));

        if (npcType == NPCType.PLAYER) {
            Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.getCacheMethod().invoke(znEntity);

            ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.getCacheConstructor().newInstance(entity_id, dataWatcherObject, true));
        }

        viewers.add(player);

        if (packetPlayOutScoreboardTeam != null) ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam);
        if (hasToggleHolo) hologram.spawn(player, true);
        if (hasGlow) toggleGlow(Optional.of(player), glowName, false);

        // Update entity id
        entity_id = (Integer) ClazzCache.GET_ID_METHOD.getCacheMethod().invoke(znEntity);

        // Fix rotation
        lookAt(Optional.of(player), location.clone(), true);

        for (final Map.Entry<NPCItemSlot, Material> test : npcEquipments.entrySet())
            equip(player, test.getKey(), test.getValue());

        ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.getCacheConstructor().newInstance(this.getEntity_id(), ClazzCache.GET_DATA_WATCHER_METHOD.getCacheMethod().invoke(this.getZnEntity()), true));

        if (npcType == NPCType.PLAYER) {
            ServersNPC.getExecutor().execute(() -> {
                try {
                    hideFromTab(player);
                } catch (Exception e) {
                    try {
                        delete(player, true);
                    } catch (Exception exception) {
                        // Should not happen
                        Bukkit.getLogger().log(Level.WARNING, "Could not remove npc for player -> " + player.getName(), e);
                    }
                }
            });
        }
    }

    public void hideFromTab(final Player player) throws Exception {
        ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.getCacheConstructor().newInstance(REMOVE_PLAYER, entityPlayerArray));
    }

    public void showTab(final Player player) throws Exception {
        if (!getViewers().contains(player)) throw new UnsupportedOperationException(String.format("Player %s is not a viewer", player.getName()));

        ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.getCacheConstructor().newInstance(ADD_PLAYER, entityPlayerArray));
    }

    /**
     * Delete npc for player
     *
     * @param player to delete npc
     */
    public void delete(final Player player, boolean removeViewer) throws Exception {
        Object entityPlayerArray = Array.newInstance(int.class, 1);
        Array.set(entityPlayerArray, 0, entity_id);

        ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.getCacheConstructor().newInstance(entityPlayerArray));

        if (removeViewer) {
            hideFromTab(player);

            viewers.remove(player);
        }

        hologram.delete(player, removeViewer);
    }

    public void updatePathLocation(ZNPCPathReader znpcPathReader, Location location) throws Exception {
        ClazzCache.SET_LOCATION_METHOD.getCacheMethod().invoke(znEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        updateLoc();

        if (hologram != null)
            hologram.setLocation(location.clone().subtract(0.5, 0, 0.5), this.npcType.getHoloHeight());

        int index = znpcPathReader.getLocationList().indexOf(this.currentPathLocation);
        Vector vector = (reversePath ? znpcPathReader.getLocationList().get(Math.max(0, Math.min(znpcPathReader.getLocationList().size() - 1, index + 1))) : znpcPathReader.getLocationList().get(Math.min(znpcPathReader.getLocationList().size() - 1, (Math.max(0, index - 1))))).toVector();

        double yDiff = (location.getY() - vector.getY());
        Location direction = currentPathLocation.clone().setDirection(location.clone().subtract(vector.clone().add(new Vector(0, yDiff, 0))).clone().toVector());
        try {
            lookAt(Optional.empty(), direction, true);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Makes the npc look at the location
     *
     * @param location the location to look
     */
    public void lookAt(final Optional<Player> playerOptional, final Location location, final boolean rotation) throws Exception {
        Location direction = (rotation ? location : this.location.clone().setDirection(location.subtract(this.location.clone()).toVector()));

        boolean rotate = (rotation || hasLookAt);

        Object lookPacket = ClazzCache.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.getCacheConstructor().newInstance(entity_id, (byte) (direction.getYaw() % (rotate ? 360 : 0) * 256 / 360), (byte) (direction.getPitch() % (rotate ? 360. : 0) * 256 / 360), false);
        Object headRotationPacket = ClazzCache.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.getCacheConstructor().newInstance(znEntity, (byte) ((direction.getYaw() % 360.) * 256 / 360));

        for (Player player : getViewers()) {
            if (playerOptional.isPresent() && playerOptional.get() != player) continue;

            ReflectionUtils.sendPacket(player, lookPacket);
            ReflectionUtils.sendPacket(player, headRotationPacket);
        }
    }

    /**
     * Get clone of gameprofile for player
     *
     * @param player object
     * @return game profile for player
     */
    public GameProfile getGameProfileForPlayer(final Player player) throws Exception {
        Object craftPlayer = ClazzCache.GET_HANDLE_PLAYER_METHOD.getCacheMethod().invoke(player);
        Object gameProfileObj = ClazzCache.GET_PROFILE_METHOD.getCacheMethod().invoke(craftPlayer);

        GameProfile gameProfileClone = (GameProfile) gameProfileObj;

        GameProfile newProfile = new GameProfile(UUID.randomUUID(), "znpcs_" + getId());
        newProfile.getProperties().putAll(gameProfileClone.getProperties());

        return newProfile;
    }

    /**
     * Toggle npc name
     * <p>
     * Hide/Show
     */
    @SuppressWarnings("unchecked")
    public void toggleName(boolean fix) throws Exception {
        if (fix) hasToggleName = !hasToggleName;

        Object packetPlayOutScoreboardTeam = ClazzCache.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR.getCacheConstructor().newInstance();

        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, (Utils.isVersionNewestThan(9) ? "i" : "h"), (hasToggleName ? 0 : 1));
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", (Utils.isVersionNewestThan(13)) ? getHologram().getStringNewestVersion(null, gameProfile.getName()) : gameProfile.getName());
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", Utils.generateRandom());
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, (Utils.isVersionNewestThan(9) ? "j" : "i"), 0);

        if (Utils.isVersionNewestThan(9) && hasGlow && glowColor != null) {
            int id = (int) ClazzCache.GET_ENUM_CHAT_ID.getCacheMethod().invoke(glowColor);

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", id);

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "c", ClazzCache.GET_ENUM_CHAT_TO_STRING.getCacheMethod().invoke(glowColor));
        }

        Collection<String> collection = Collections.singletonList(gameProfile.getName());

        if (Utils.isVersionNewestThan(9)) ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "h", collection);
        else ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", collection);

        this.packetPlayOutScoreboardTeam = packetPlayOutScoreboardTeam;

        viewers.forEach(player -> ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam));
    }

    /**
     * @param name   Method name
     * @param values
     */
    public void customize(final String name, final List values) {
        this.customizationMap.put(name, values);

        // Update entity for current viewers
        try {
            Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.getCacheConstructor().newInstance(this.getEntity_id(), ClazzCache.GET_DATA_WATCHER_METHOD.getCacheMethod().invoke(this.getZnEntity()), true);

            viewers.forEach(player -> ReflectionUtils.sendPacket(player, packet));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            //e.printStackTrace();
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
     * @return get actions
     */
    public List<String> getActions() {
        return actions;
    }

    /**
     * Set actions
     *
     * @param actions set
     */
    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    /**
     * Set lines
     *
     * @param lines set
     */
    public void setLines(String lines) {
        this.lines = lines;
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
        return (hasPath() && currentPathLocation != null ? currentPathLocation : location);
    }

    /**
     * @return npc item slots with mat name
     */
    public EnumMap<NPCItemSlot, Material> getNpcItemSlotMaterialHashMap() {
        return npcEquipments;
    }

    /**
     * @return entity
     */
    public Object getZnEntity() {
        return znEntity;
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

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public boolean isReversePath() {
        return isReversePath;
    }

    public void setReversePath(boolean reversePath) {
        isReversePath = reversePath;
    }

    public void setPath(ZNPCPathReader znpcPathReader) {
        String name = "none";
        if (znpcPathReader != null) name = znpcPathReader.getName();

        this.npcPath = znpcPathReader;
        this.pathName = name;
    }

    public ZNPCPathReader getPathReader() {
        return npcPath;
    }

    public Map<String, List> getCustomizationMap() {
        return customizationMap;
    }

    public boolean hasPath() {
       return (getPathName() == null || getPathName().equalsIgnoreCase("none"));
    }

    /**
     * Update customization for npc
     *
     * @param customizationMap
     */
    public void setCustomizationMap(HashMap<String, List> customizationMap) {
        this.customizationMap = customizationMap;
        for (Map.Entry<String, List> stringEntry : customizationMap.entrySet()) {
            if (this.npcType.getCustomizationMethods().containsKey(stringEntry.getKey())) {
                try {
                    npcType.invokeMethod(stringEntry.getKey(), getZnEntity(), NPCType.arrayToPrimitive((String[]) stringEntry.getValue().stream().map(Objects::toString).toArray(String[]::new), npcType.getCustomizationMethods().get(stringEntry.getKey())));
                } catch (Exception e) {
                    // Remove customization from map
                    customizationMap.remove(stringEntry.getKey());
                }
            }
        }
    }

    public Object getGlowColor(final String string) throws Exception {
        Class<?> clazzCache = ClazzCache.ENUM_CHAT_FORMAT_CLASS.getCacheClass();

        try {
            return clazzCache.getField(string.trim().toUpperCase()).get(null);
        } catch (NoSuchFieldException e) {
            return clazzCache.getField("WHITE").get(null);
        }
    }
}