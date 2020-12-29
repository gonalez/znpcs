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
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
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
import java.util.logging.Level;

public class NPC {

    // Exclude
    @Expose(serialize = false)
    protected Object entityPlayerArray;
    protected Object enumPlayerInfoAction;
    protected Object glowColor;
    protected Object dataWatcherRegistryEnum;
    protected Object nmsWorld;
    protected Object nmsServer;
    protected Object packetPlayOutPlayerInfoConstructor;
    protected Object znEntity;
    protected Object packetPlayOutScoreboardTeam;

    @Expose(serialize = false)
    protected GameProfile gameProfile;
    @Expose(serialize = false)
    protected Hologram hologram;

    @Expose(serialize = false)
    protected HashSet<Player> viewers;

    @Expose(serialize = false)
    protected int entity_id;

    // Serialize
    @Expose
    protected int id;
    @Expose
    protected boolean hasGlow = false;
    @Expose
    protected boolean hasToggleName = false;
    @Expose
    protected boolean hasToggleHolo = true;
    @Expose
    protected boolean hasLookAt = false;
    @Expose
    protected boolean hasMirror = false;

    @Expose
    protected Location location;

    @Expose
    protected List<String> actions;

    @Expose
    protected EnumMap<NPCItemSlot, Material> npcEquipments;

    @Expose
    protected String skin,signature;

    @Expose
    protected String glowName = "WHITE";

    @Expose
    protected final boolean save;

    @Expose
    protected NPCType npcType;

    @Expose
    protected String lines;

    /**
     * Init of the necessary functionalities for the npc
     *
     * @param id the npc id
     * @param skin the skin value
     * @param signature the skin signature
     * @param location the location for the npc
     */
    public NPC(final int id, final String lines, final String skin , final String signature , final Location location , NPCType npcType, EnumMap<NPCItemSlot , Material> npcEquipments, boolean save) throws Exception {
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

        nmsServer = ClazzCache.GET_SERVER_METHOD.method.invoke(Bukkit.getServer());
        nmsWorld = ClazzCache.GET_HANDLE_METHOD.method.invoke(location.getWorld());

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
     * Toggle the npc glow
     */
    public void toggleGlow(final Optional<Player> playerOptional, final String color, boolean fix) throws Exception {
        if (!Utils.isVersionNewestThan(9)) throw new UnsupportedOperationException("Version not supported");
        if (fix) hasGlow = !hasGlow;

        Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.method.invoke(znEntity);

        ClazzCache.SET_DATA_WATCHER_METHOD.method.invoke(dataWatcherObject, ClazzCache.DATA_WATCHER_OBJECT_CONSTRUCTOR.constructor.newInstance(0, dataWatcherRegistryEnum) , (hasGlow ? (byte) 0x40 : (byte) 0x0));

        Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.constructor.newInstance(entity_id , dataWatcherObject , true);

        this.glowColor = getGlowColor(color);
        this.glowName = color;

        playerOptional.ifPresent(player -> ReflectionUtils.sendPacket(player, packet));
        // Update glow color
        toggleName(false);
    }

    public Object getGlowColor(final String string) throws Exception {
        final Class<?> clazzCache = ClazzCache.ENUM_CHAT_FORMAT_CLASS.aClass;

        try {
            return clazzCache.getField(string.trim().toUpperCase()).get(null);
        } catch (NoSuchFieldException e) {
            return clazzCache.getField("WHITE").get(null);
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
        Object stack = ClazzCache.AS_NMS_COPY_METHOD.method.invoke(ClazzCache.CRAFT_ITEM_STACK_CLASS.aClass, new ItemStack(material));

        Constructor<?> getPacketPlayOutNamedEntitySpawnConstructor;

        Object v16b = null;

        if (!Utils.isVersionNewestThan(9)) getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.getConstructor(int.class , int.class , ClazzCache.ITEM_STACK_CLASS.aClass);
        else {
            if (Utils.isVersionNewestThan(16)) {
                getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.getConstructor(int.class , List.class);

                v16b = ReflectionUtils.getValue(ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.newInstance() , "b");
            }
            else getPacketPlayOutNamedEntitySpawnConstructor = ClazzCache.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CLASS.aClass.getConstructor(int.class , ClazzCache.ENUM_ITEM_SLOT_CLASS.aClass , ClazzCache.ITEM_STACK_CLASS.aClass);
        }
        npcEquipments.put(slot , material);

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

        final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "znpc_" + getId());
        gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        Object gameProfileObj = ClazzCache.GET_PROFILE_METHOD.method.invoke(znEntity);

        ReflectionUtils.setValue(gameProfileObj , "id", UUID.randomUUID());
        ReflectionUtils.setValue(gameProfileObj , "properties", gameProfile.getProperties());

        final Iterator<Player> it = this.getViewers().iterator();

        while (it.hasNext())  {
            final Player player = it.next();

            this.delete(player , false);

            it.remove();
        }
    }

    public void fixSkin() throws Exception {
        Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.method.invoke(znEntity);
        if (Utils.isVersionNewestThan(9)) {
            dataWatcherRegistryEnum = ClazzCache.DATA_WATCHER_REGISTER_ENUM_FIELD.field.get(null);

            int version = Utils.getVersion();

            ClazzCache.SET_DATA_WATCHER_METHOD.method.invoke(dataWatcherObject , ClazzCache.DATA_WATCHER_OBJECT_CONSTRUCTOR.constructor.newInstance(version < 11 ? 10 : (version == 11 || version == 12 || version == 13 ? 13 : (version == 14) ? 15 : 16), dataWatcherRegistryEnum) , (byte) 127);
        } else ClazzCache.WATCH_DATA_WATCHER_METHOD.method.invoke(dataWatcherObject , 10 , (byte) 127);
    }

    public void changeType(final NPCType npcType) throws Exception {
        Constructor<?> constructor = (npcType != NPCType.PLAYER ? (Utils.isVersionNewestThan(13) ? npcType.aClass.aClass.getConstructor(npcType.entityType.getClass(), ClazzCache.WORLD_CLASS.aClass) : npcType.aClass.aClass.getConstructor(ClazzCache.WORLD_CLASS.aClass)) : null);

        znEntity = (npcType == NPCType.PLAYER ? ClazzCache.PLAYER_CONSTRUCTOR.constructor.newInstance(nmsServer , nmsWorld , gameProfile , ClazzCache.PLAYER_INTERACT_MANAGER_CONSTRUCTOR.constructor.newInstance(nmsWorld)) : (Utils.isVersionNewestThan(13) ? constructor.newInstance(npcType.entityType, nmsWorld) : constructor.newInstance(nmsWorld)));

        if (npcType == NPCType.PLAYER) {
            entityPlayerArray = Array.newInstance(ClazzCache.ENTITY_PLAYER_CLASS.aClass, 1);
            Array.set(entityPlayerArray, 0, znEntity);

            packetPlayOutPlayerInfoConstructor = ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.constructor.newInstance(enumPlayerInfoAction , entityPlayerArray);

            // Fix second layer skin for entity player
            fixSkin();
        }

        this.npcType = npcType;

        setLocation(this.location);

        // Update new type for viewers
        final Iterator<Player> it = this.getViewers().iterator();

        while (it.hasNext())  {
            final Player player = it.next();

            this.delete(player , false);

            it.remove();
        }

        // Update new id
        entity_id = (Integer) ClazzCache.GET_ID_METHOD.method.invoke(znEntity);
    }

    /**
     * Spawn the npc for player
     * @param player to show npc
     */
    public void spawn(final Player player) throws Exception {
        toggleName( false);

        if (npcType == NPCType.PLAYER && isHasMirror()) {
            final GameProfile gameProfile = getGameProfileForPlayer(player);

            Object gameProfileObj = ClazzCache.GET_PROFILE_METHOD.method.invoke(znEntity);

            ReflectionUtils.setValue(gameProfileObj , "id" , UUID.randomUUID());
            ReflectionUtils.setValue(gameProfileObj , "properties" , gameProfile.getProperties());
        }

        // Update location
        setLocation(this.location);

        if (npcType == NPCType.PLAYER) ReflectionUtils.sendPacket(player ,packetPlayOutPlayerInfoConstructor);

        ReflectionUtils.sendPacket(player, (npcType == NPCType.PLAYER ? ClazzCache.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.constructor.newInstance(znEntity) : ((npcType.id < 0 ? npcType.constructor.newInstance(znEntity) : npcType.constructor.newInstance(znEntity, npcType.id)))));

        if (npcType == NPCType.PLAYER) {
            Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.method.invoke(znEntity);

            ReflectionUtils.sendPacket(player , ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.constructor.newInstance(entity_id , dataWatcherObject , true));
        }

        viewers.add(player);

        if (packetPlayOutScoreboardTeam != null) ReflectionUtils.sendPacket(player , packetPlayOutScoreboardTeam);
        if (hasToggleHolo) hologram.spawn(player , true);
        if (hasGlow) toggleGlow(Optional.of(player), glowName ,false);

        // Update entity id
        entity_id = (Integer) ClazzCache.GET_ID_METHOD.method.invoke(znEntity);

        // Fix rotation
        lookAt(player , location.clone() , true);

        for (final Map.Entry<NPCItemSlot, Material> test : npcEquipments.entrySet()) equip(player , test.getKey() , test.getValue());

        if (npcType == NPCType.PLAYER) {
            ServersNPC.getExecutor().execute(() -> {
                try {
                    hideFromTablist(player);
                } catch (Exception e) {
                    try {
                        delete(player, true);
                    } catch (Exception exception) {
                        // Should not happen
                        Bukkit.getLogger().log(Level.WARNING , "Could not remove npc for player -> " + player.getName(), e);
                    }
                }
            });
        }
    }

    public void hideFromTablist(final Player player) throws Exception {
        Object enumPlayerInfoAction = ClazzCache.REMOVE_PLAYER_FIELD.field.get(null);

        Object packetPlayOutPlayerInfoConstructor = ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.constructor.newInstance(enumPlayerInfoAction , entityPlayerArray);

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

        ReflectionUtils.sendPacket(player , ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.constructor.newInstance(entityPlayerArray));

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

        if (!fix) ReflectionUtils.sendPacket(player , ClazzCache.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.constructor.newInstance(entity_id , (byte) (direction.getYaw() % (!direction.equals(this.location) ? 360 : 0) * 256/360) ,  (byte) (direction.getPitch() % (!direction.equals(this.location) ? 360. : 0) * 256/360) , false));
        ReflectionUtils.sendPacket(player , ClazzCache.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.constructor.newInstance(znEntity , (byte) (((direction.getYaw()) * 256.0F) / 360.0F)));
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
        Object craftPlayer = ClazzCache.GET_HANDLE_PLAYER_METHOD.method.invoke(player);
        Object gameProfileObj = ClazzCache.GET_PROFILE_METHOD.method.invoke(craftPlayer);

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
        if (fix) hasToggleName = !hasToggleName;

        Object packetPlayOutScoreboardTeam = ClazzCache.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR.constructor.newInstance();

        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, (Utils.isVersionNewestThan(9) ? "i" : "h"), (hasToggleName ? 0 : 1));
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", (Utils.isVersionNewestThan(13)) ? getHologram().getStringNewestVersion(null, gameProfile.getName()) : gameProfile.getName());
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", Utils.generateRandom());
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
        ReflectionUtils.setValue(packetPlayOutScoreboardTeam, (Utils.isVersionNewestThan(9) ? "j" : "i"), 0);

        if (Utils.isVersionNewestThan(9) && hasGlow && glowColor != null){
            int id = (int) ClazzCache.GET_ENUM_CHAT_ID.method.invoke(glowColor);

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", id);

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "c", ClazzCache.GET_ENUM_CHAT_TO_STRING.method.invoke(glowColor));
        }

        Collection<String> collection = Collections.singletonList(gameProfile.getName());

        if (Utils.isVersionNewestThan(9)) ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "h", collection);
        else ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", collection);

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
    public EnumMap<NPCItemSlot, Material> getNpcItemSlotMaterialHashMap() {
        return npcEquipments;
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
        Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.constructor.newInstance(znEntity);

        viewers.forEach(player -> ReflectionUtils.sendPacket(player , packet));
    }

    /**
     * Set new location for npc
     *
     * @param location new
     */
    public void setLocation(Location location) throws Exception {
        this.location = new Location(location.getWorld(), location.getBlockX() + 0.5D, location.getY(), location.getBlockZ() + 0.5D, location.getYaw() , location.getPitch());

        ClazzCache.SET_LOCATION_METHOD.method.invoke(znEntity , this.location.getX(),  this.location.getY(), this.location.getZ(), location.getYaw() , location.getPitch());

        if (hologram != null) hologram.setLocation(this.location.clone().subtract(0.5, 0 , 0.5), this.npcType.holoHeight);

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
}