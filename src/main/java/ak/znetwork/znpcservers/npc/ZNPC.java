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
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import ak.znetwork.znpcservers.utils.objects.SkinFetch;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ZNPC {

    // <-- Serialize = true -->

    @Expose @Getter private final int id;

    @Expose @Getter @Setter private boolean hasGlow = false;
    @Expose @Getter @Setter private boolean hasToggleName = false;
    @Expose @Getter @Setter private boolean hasToggleHolo = true;
    @Expose @Getter @Setter private boolean hasLookAt = false;
    @Expose @Getter @Setter private boolean hasMirror = false;
    @Expose @Getter @Setter private boolean isReversePath = false;

    @Expose @Getter @Setter private boolean save;

    @Expose @Getter @Setter private String skin, signature;
    @Expose @Getter private String glowName = "WHITE";
    @Expose @Getter @Setter private String lines;
    @Expose @Getter @Setter private String pathName;

    @Expose @Getter @Setter private NPCType npcType;
    @Expose private Location location;


    @Expose @Getter @Setter private List<String> actions;
    @Expose @Getter @Setter private EnumMap<NPCItemSlot, Material> npcEquipments;
    @Expose @Getter private Map<String, List> customizationMap;

    // <-- Serialize = false -->

    @Getter private final ServersNPC serversNPC;

    // Cache
    private Object entityPlayerArray;
    private Object glowColor;
    private Object dataWatcherRegistryEnum;
    private Object packetPlayOutPlayerInfoConstructor;
    private Object znEntity;
    private Object packetPlayOutScoreboardTeam;

    private final GameProfile gameProfile;

    @Getter @Setter private String worldName;

    @Getter private final HashSet<Player> viewers;

    @Getter @Setter private int entity_id;

    @Getter @Setter private Hologram hologram;

    // Path
    private ZNPCPathReader npcPath;
    private Location currentPathLocation;

    private int currentEntryPath = 0;
    private boolean reversePath = false;

    /**
     * Init of the necessary functionalities for the npc
     *
     * @param id        the npc id
     * @param skin      the skin value
     * @param signature the skin signature
     * @param location  the location for the npc
     */
    public ZNPC(final ServersNPC serversNPC, final int id, final String lines, final String skin, final String signature, final Location location, NPCType npcType, EnumMap<NPCItemSlot, Material> npcEquipments, boolean save) {
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

        this.gameProfile = new GameProfile(UUID.randomUUID(), "znpc_" + getId());
        this.gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        changeType(npcType);
        toggleName(true);
    }

    /**
     * Toggle the npc glow
     */
    public void toggleGlow(final Optional<Player> playerOptional, final String color, boolean fix) {
        if (!Utils.isVersionNewestThan(9)) throw new UnsupportedOperationException("Version not supported");
        if (fix) hasGlow = !hasGlow;

        try {
            Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.getCacheMethod().invoke(znEntity);

            ClazzCache.SET_DATA_WATCHER_METHOD.getCacheMethod().invoke(dataWatcherObject, ClazzCache.DATA_WATCHER_OBJECT_CONSTRUCTOR.getCacheConstructor().newInstance(0, dataWatcherRegistryEnum), (hasGlow ? (byte) 0x40 : (byte) 0x0));

            Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.getCacheConstructor().newInstance(entity_id, dataWatcherObject, true);

            this.glowColor = getGlowColor(color);
            this.glowName = color;

            playerOptional.ifPresent(player -> ReflectionUtils.sendPacket(player, packet));

            // Update glow color
            toggleName(false);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
            throw new AssertionError(operationException);
        }
    }

    public void handlePath() {
        if (npcPath == null) return;

        if (isReversePath) {
            if (currentEntryPath <= 0) reversePath = false;
            else if (currentEntryPath >= npcPath.getLocationList().size() - 1) reversePath = true;
        }

        currentPathLocation = npcPath.getLocationList().get(Math.min(npcPath.getLocationList().size() - 1, currentEntryPath));

        if (!reversePath) currentEntryPath++;
        else currentEntryPath--;

        updatePathLocation(npcPath, currentPathLocation);
    }

    /**
     * Update new location
     */
    public void updateLoc() {
        try {
            Object packet = ClazzCache.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.getCacheConstructor().newInstance(znEntity);

            viewers.forEach(player -> ReflectionUtils.sendPacket(player, packet));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Set new location for npc
     *
     * @param location new
     */
    public void setLocation(Location location) {
        try {
            ClazzCache.SET_LOCATION_METHOD.getCacheMethod().invoke(znEntity, this.location.getX(), this.location.getY(), this.location.getZ(), location.getYaw(), location.getPitch());

            this.location = new Location(location.getWorld(), location.getBlockX() + 0.5D, location.getY(), location.getBlockZ() + 0.5D, location.getYaw(), location.getPitch());

            if (hologram != null)
                hologram.setLocation(this.location.clone().subtract(0.5, 0, 0.5), this.npcType.getHoloHeight());

            lookAt(Optional.empty(), location, true);

            updateLoc();
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Equip npc
     *
     * @param player   receiver
     * @param slot     item slot
     * @param material material
     */
    public void equip(final Player player, NPCItemSlot slot, Material material) {
        try {
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
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Update new skin for new players
     *
     * @param skinFetch value
     */
    public void changeSkin(final SkinFetch skinFetch) {
        setSkin(skinFetch.getValue());
        setSignature(skinFetch.getSignature());

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "znpc_" + getId());
        gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        try {
            Object gameProfileObj = ClazzCache.GET_PROFILE_METHOD.getCacheMethod().invoke(znEntity);

            ReflectionUtils.setValue(gameProfileObj, "id", UUID.randomUUID());
            ReflectionUtils.setValue(gameProfileObj, "properties", gameProfile.getProperties());

            final Iterator<Player> it = this.getViewers().iterator();
            while (it.hasNext()) {
                final Player player = it.next();

                delete(player, false);

                it.remove();
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    public void fixSkin() {
        try {
            Object dataWatcherObject = ClazzCache.GET_DATA_WATCHER_METHOD.getCacheMethod().invoke(znEntity);

            if (Utils.isVersionNewestThan(9)) {
                dataWatcherRegistryEnum = ClazzCache.DATA_WATCHER_REGISTER_ENUM_FIELD.getCacheField().get(null);

                int version = Utils.getVersion();

                ClazzCache.SET_DATA_WATCHER_METHOD.getCacheMethod().invoke(dataWatcherObject, ClazzCache.DATA_WATCHER_OBJECT_CONSTRUCTOR.getCacheConstructor().newInstance(version < 11 ? 10 : (version == 11 || version == 12 || version == 13 ? 13 : (version == 14) ? 15 : 16), dataWatcherRegistryEnum), (byte) 127);
            } else ClazzCache.WATCH_DATA_WATCHER_METHOD.getCacheMethod().invoke(dataWatcherObject, 10, (byte) 127);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
            throw new AssertionError(operationException);
        }
    }

    public void changeType(final NPCType npcType) {
        try {
            Object nmsWorld = ClazzCache.GET_HANDLE_METHOD.getCacheMethod().invoke(location.getWorld());

            znEntity = (npcType == NPCType.PLAYER ? ClazzCache.PLAYER_CONSTRUCTOR.getCacheConstructor().newInstance(ClazzCache.GET_SERVER_METHOD.getCacheMethod().invoke(Bukkit.getServer()), nmsWorld, gameProfile, ClazzCache.PLAYER_INTERACT_MANAGER_CONSTRUCTOR.getCacheConstructor().newInstance(nmsWorld)) : (Utils.isVersionNewestThan(13) ? npcType.getConstructor().newInstance(npcType.getEntityType(), nmsWorld) : npcType.getConstructor().newInstance(nmsWorld)));

            if (npcType == NPCType.PLAYER) {
                entityPlayerArray = Array.newInstance(ClazzCache.ENTITY_PLAYER_CLASS.getCacheClass(), 1);
                Array.set(entityPlayerArray, 0, znEntity);

                packetPlayOutPlayerInfoConstructor = ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.getCacheConstructor().newInstance(ClazzCache.ADD_PLAYER_FIELD.getCacheField().get(null), entityPlayerArray);

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
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Spawn the npc for player
     *
     * @param player to show npc
     */
    public void spawn(final Player player) {
        toggleName(false);

        try {
            boolean isPlayer = (npcType == NPCType.PLAYER);

            if (isPlayer && isHasMirror()) {
                GameProfile gameProfile = getGameProfileForPlayer(player);

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

            if (isPlayer) ServersNPC.getExecutor().execute(() -> hideFromTab(player));

            viewers.add(player);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void hideFromTab(final Player player) {
        try {
            ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.getCacheConstructor().newInstance(ClazzCache.REMOVE_PLAYER_FIELD.getCacheField().get(null), entityPlayerArray));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    public void showTab(final Player player) {
        if (!getViewers().contains(player))
            throw new UnsupportedOperationException(String.format("Player %s is not a viewer", player.getName()));

        try {
            ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.getCacheConstructor().newInstance(ClazzCache.ADD_PLAYER_FIELD.getCacheField().get(null), entityPlayerArray));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Delete npc for player
     *
     * @param player to delete npc
     */
    public void delete(final Player player, boolean removeViewer) {
        try {
            ReflectionUtils.sendPacket(player, ClazzCache.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.getCacheConstructor().newInstance(new int[]{entity_id}));

            hideFromTab(player);

            if (removeViewer)
                viewers.remove(player);

            hologram.delete(player, removeViewer);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    public void updatePathLocation(ZNPCPathReader znpcPathReader, Location location) {
        try {
            ClazzCache.SET_LOCATION_METHOD.getCacheMethod().invoke(znEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            updateLoc();

            if (hologram != null)
                hologram.setLocation(location.clone().subtract(0.5, 0, 0.5), this.npcType.getHoloHeight());

            int index = znpcPathReader.getLocationList().indexOf(this.currentPathLocation);
            Vector vector = (reversePath ? znpcPathReader.getLocationList().get(Math.max(0, Math.min(znpcPathReader.getLocationList().size() - 1, index + 1))) : znpcPathReader.getLocationList().get(Math.min(znpcPathReader.getLocationList().size() - 1, (Math.max(0, index - 1))))).toVector();

            double yDiff = (location.getY() - vector.getY());
            Location direction = currentPathLocation.clone().setDirection(location.clone().subtract(vector.clone().add(new Vector(0, yDiff, 0))).clone().toVector());

            lookAt(Optional.empty(), direction, true);
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Makes the npc look at the location
     *
     * @param location the location to look
     */
    public void lookAt(final Optional<Player> playerOptional, final Location location, final boolean rotation) {
        Location direction = (rotation ? location : this.location.clone().setDirection(location.subtract(this.location.clone()).toVector()));
        boolean rotate = (rotation || hasLookAt);

        try {
            Object lookPacket = ClazzCache.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.getCacheConstructor().newInstance(entity_id, (byte) (direction.getYaw() % (rotate ? 360 : 0) * 256 / 360), (byte) (direction.getPitch() % (rotate ? 360. : 0) * 256 / 360), false);
            Object headRotationPacket = ClazzCache.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.getCacheConstructor().newInstance(znEntity, (byte) ((direction.getYaw() % 360.) * 256 / 360));

            for (Player player : getViewers()) {
                if (playerOptional.isPresent() && playerOptional.get() != player) continue;

                ReflectionUtils.sendPacket(player, lookPacket);
                ReflectionUtils.sendPacket(player, headRotationPacket);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Get clone of game-profile for player
     *
     * @param player object
     * @return game profile for player
     */
    public GameProfile getGameProfileForPlayer(final Player player) {
        try {
            Object playerProfile = ClazzCache.GET_PROFILE_METHOD.getCacheMethod().invoke(ClazzCache.GET_HANDLE_PLAYER_METHOD.getCacheMethod().invoke(player));

            GameProfile newProfile = new GameProfile(UUID.randomUUID(), "znpcs_" + getId());
            newProfile.getProperties().putAll(((GameProfile) playerProfile).getProperties());

            return newProfile;
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Toggle npc name
     * <p>
     * Hide/Show
     */
    public void toggleName(boolean toggle) {
        if (toggle) hasToggleName = !hasToggleName;

        try {
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
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
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
            throw new AssertionError(e);
        }
    }

    public void toggleLookAt() {
        hasLookAt = !hasLookAt;
    }

    public void toggleHolo() {
        hasToggleHolo = !hasToggleHolo;

        if (!hasToggleHolo) viewers.forEach(player -> hologram.delete(player, true));
        else viewers.forEach(player -> hologram.spawn(player, true));
    }

    public void toggleMirror() {
        hasMirror = !hasMirror;
    }

    public Location getLocation() {
        return (hasPath() && currentPathLocation != null ? currentPathLocation : location);
    }

    public Object getZnEntity() {
        return znEntity;
    }

    public void setPath(ZNPCPathReader znpcPathReader) {
        String name = "none";
        if (znpcPathReader != null) name = znpcPathReader.getName();

        this.npcPath = znpcPathReader;
        this.pathName = name;
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

    public Object getGlowColor(String string) {
        try {
            return ClazzCache.ENUM_CHAT_FORMAT_CLASS.getCacheClass().getField(string.trim().toUpperCase()).get(null);
        } catch (NoSuchFieldException e) {
            // Get Default Glow-Color
            return getGlowColor("WHITE");
        } catch (IllegalAccessException accessException) {
            throw new AssertionError(accessException);
        }
    }
}