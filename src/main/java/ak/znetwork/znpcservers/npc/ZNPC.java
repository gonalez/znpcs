package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;
import ak.znetwork.znpcservers.npc.skin.ZNPCSkin;
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ZNPC {

    /**
     * The npc identifier.
     */
    @Expose private final int id;

    /**
     * Toggle variables.
     */
    @Expose @Setter private boolean hasGlow, hasToggleName, hasLookAt, hasMirror, isReversePath = false;
    @Expose @Setter private boolean hasToggleHolo = true;

    /**
     * Determines if the npc should be saved.
     */
    @Expose @Setter private boolean save;

    /**
     * The skin value & signature.
     */
    @Expose @Setter private String skin, signature;

    /**
     * The hologram lines.
     */
    @Expose @Setter private String lines;

    /**
     * The path name.
     */
    @Expose @Setter private String pathName;

    /**
     * The glow name.
     */
    @Expose @Setter private String glowName = "WHITE";

    /**
     * The npc location
     */
    @Expose private Location location;

    /**
     * The npc entity type.
     */
    @Expose @Setter
    private NPCType npcType;

    /**
     * The actions to be executed when the npc is clicked.
     */
    @Expose @Setter private List<String> actions;

    /**
     * The npc equipment.
     */
    @Expose @Setter private EnumMap<NPCItemSlot, Material> npcEquipments;

    /**
     * The npc customizations.
     */
    @Expose private Map<String, List> customizationMap;

    /**
     * The npc entity id.
     */
    @Setter private int entity_id;

    /**
     * The current world for the npc.
     */
    @Setter private String worldName;

    /**
     * The hologram.
     */
    @Setter private Hologram hologram;

    /**
     * A list of players the players who can see the npc.
     */
    @Setter private HashSet<Player> viewers;

    /**
     * Cache reflection variables.
     */
    private Object glowColor;
    private Object dataWatcherRegistryEnum;
    private Object packetPlayOutPlayerInfoConstructor;
    private Object znEntity;
    private Object packetPlayOutScoreboardTeam;

    /**
     * The current profile of the npc.
     */
    private GameProfile gameProfile;

    /**
     * The path reader.
     */
    private ZNPCPathReader npcPath;

    /**
     * The path location.
     */
    private Location currentPathLocation;

    /**
     * Determines the current path location.
     */
    private int currentEntryPath = 0;

    /**
     * Determines if path is running backwards or forwards.
     */
    private boolean reversePath = false;

    /**
     * The plugin instance.
     */
    private final ServersNPC serversNPC;

    /**
     * Creates a new NPC.
     *
     * @param id            The npc id.
     * @param lines         The hologram lines.
     * @param skin          The skin value.
     * @param signature     The skin signature.
     * @param location      The npc location.
     * @param npcType       The npc entity type.
     * @param npcEquipments The npc equipments.
     * @param save          Checks if npc will be saved on data.
     */
    public ZNPC(ServersNPC serversNPC,
                int id,
                String lines,
                String skin,
                String signature,
                Location location,
                NPCType npcType,
                EnumMap<NPCItemSlot, Material> npcEquipments,
                boolean save) {
        this.serversNPC = serversNPC;
        this.id = id;
        this.lines = (this.hologram = new Hologram(location, lines.split(":"))).getLinesFormatted();
        this.skin = skin;
        this.signature = signature;
        this.location = location;
        this.npcType = npcType;
        this.npcEquipments = npcEquipments;
        this.save = save;

        this.init();
    }

    /**
     * Initialization of all necessary functions for the npc.
     * Called when a npc is created for the first time.
     */
    public void init() {
        this.viewers = new HashSet<>();
        this.customizationMap = new HashMap<>();

        this.actions = new ArrayList<>();

        this.gameProfile = new GameProfile(UUID.randomUUID(), "znpc_" + getId());
        this.gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        this.changeType(npcType);
        this.toggleName(true);
    }

    /**
     * Set/Update the npc glow.
     *
     * @param color The glow color.
     * @param toggle Toggles (on/off) the glow of the npc.
     */
    public void toggleGlow(String color, boolean toggle) {
        if (!Utils.versionNewer(9)) throw new UnsupportedOperationException("Version not supported");

        if (toggle)
            hasGlow = !hasGlow;

        try {
            Object npcDataWatcher = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(znEntity);
            ClassTypes.SET_DATA_WATCHER_METHOD.invoke(npcDataWatcher, ClassTypes.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(0, dataWatcherRegistryEnum), (hasGlow ? (byte) 0x40 : (byte) 0x0));

            Object packet = ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(entity_id, npcDataWatcher, true);
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, packet));

            glowColor = getGlowColor(color);
            glowName = color;

            // Updates new glow color
            toggleName(false);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Updates the npc location.
     */
    public void updateLocation() {
        try {
            Object npcTeleportPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(znEntity);
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, npcTeleportPacket));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Set new location for the npc.
     *
     * @param location The new location.
     */
    public void setLocation(Location location) {
        try {
            ClassTypes.SET_LOCATION_METHOD.invoke(znEntity, location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5, location.getYaw(), location.getPitch());
            this.location = new Location(location.getWorld(), location.getBlockX() + 0.5D, location.getY(), location.getBlockZ() + 0.5D, location.getYaw(), location.getPitch());

            if (hologram != null)
                hologram.setLocation(location, npcType.getHoloHeight());

            lookAt(Optional.empty(), location, true);
            updateLocation();
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Equip the npc.
     *
     * @param player   The player to send the new npc equipment.
     * @param slot     The item slot (hand,helmet,...etc)
     * @param material The item to equip.
     */
    public void equip(Player player, NPCItemSlot slot, Material material) {
        try {
            Object item = ClassTypes.AS_NMS_COPY_METHOD.invoke(ClassTypes.CRAFT_ITEM_STACK_CLASS, new ItemStack(material));

            Object equipPacket;
            if (!Utils.versionNewer(9)) {
                equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD.newInstance(entity_id, slot.getEquipmentSlot(), item);
            } else {
                if (Utils.versionNewer(16)) {
                    List<Pair<?, ?>> pairs = (List<Pair<?, ?>>) ReflectionUtils.getValue(ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT.newInstance(), "b");
                    pairs.add(new Pair<>(ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[slot.getEquipmentSlot() + 1], item));

                    equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEW.newInstance(entity_id, pairs);
                } else {
                    equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD.newInstance(entity_id, ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[slot.getEquipmentSlot() + 1], item);
                }
            }

            npcEquipments.put(slot, material);

            if (player != null) ReflectionUtils.sendPacket(player, equipPacket);
            else viewers.forEach(players -> ReflectionUtils.sendPacket(players, equipPacket));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Updates npc skin.
     *
     * @param skinFetch The skin to set.
     */
    public void changeSkin(ZNPCSkin skinFetch) {
        setSkin(skinFetch.getValue());
        setSignature(skinFetch.getSignature());

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "znpc_" + getId());
        gameProfile.getProperties().put("textures", new Property("textures", skin, signature));

        try {
            Object gameProfileObj = ClassTypes.GET_PROFILE_METHOD.invoke(znEntity);

            ReflectionUtils.setValue(gameProfileObj, "id", UUID.randomUUID());
            ReflectionUtils.setValue(gameProfileObj, "properties", gameProfile.getProperties());

            deleteViewers();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Enables second layer of skin for the npc.
     */
    public void setSecondLayerSkin() {
        try {
            Object dataWatcherObject = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(znEntity);

            if (Utils.versionNewer(9)) {
                dataWatcherRegistryEnum = ClassTypes.DATA_WATCHER_REGISTER_ENUM_FIELD.get(null);

                int version = Utils.BUKKIT_VERSION;

                ClassTypes.SET_DATA_WATCHER_METHOD.invoke(dataWatcherObject, ClassTypes.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(version < 11 ? 10 : (version == 11 || version == 12 || version == 13 ? 13 : (version == 14) ? 15 : 16), dataWatcherRegistryEnum), (byte) 127);
            } else ClassTypes.WATCH_DATA_WATCHER_METHOD.invoke(dataWatcherObject, 10, (byte) 127);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
            throw new AssertionError(operationException);
        }
    }


    /**
     * Changes the entity type of the npc.
     *
     * @param npcType The new entity type.
     */
    public void changeType(NPCType npcType) {
        try {
            Object nmsWorld = ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(location.getWorld());
            znEntity = (npcType == NPCType.PLAYER ? ClassTypes.PLAYER_CONSTRUCTOR.newInstance(ClassTypes.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, gameProfile, (Utils.versionNewer(14) ? ClassTypes.PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR : ClassTypes.PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR).newInstance(nmsWorld)) : (Utils.versionNewer(13) ? npcType.getConstructor().newInstance(npcType.getEntityType(), nmsWorld) : npcType.getConstructor().newInstance(nmsWorld)));

            if (npcType == NPCType.PLAYER) {
                packetPlayOutPlayerInfoConstructor = ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.ADD_PLAYER_FIELD.get(null), Collections.singletonList(znEntity));

                // Fix second layer skin for entity player
                setSecondLayerSkin();
            }

            this.npcType = npcType;

            setLocation(location);

            // Update new type for viewers
            deleteViewers();

            // Update new entity id.
            entity_id = (Integer) ClassTypes.GET_ENTITY_ID.invoke(znEntity);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Spawn the npc for player.
     *
     * @param player The player to see the npc.
     */
    public void spawn(Player player) {
        toggleName(false);

        try {
            boolean npcIsPlayer = (npcType == NPCType.PLAYER);
            if (npcIsPlayer && isHasMirror()) {
                GameProfile gameProfile = getGameProfileForPlayer(player);

                Object gameProfileObj = ClassTypes.GET_PROFILE_METHOD.invoke(znEntity);

                ReflectionUtils.setValue(gameProfileObj, "id", UUID.randomUUID());
                ReflectionUtils.setValue(gameProfileObj, "properties", gameProfile.getProperties());
            }

            if (npcIsPlayer) ReflectionUtils.sendPacket(player, packetPlayOutPlayerInfoConstructor);
            ReflectionUtils.sendPacket(player, (npcType == NPCType.PLAYER ? ClassTypes.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.newInstance(znEntity) : ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(znEntity)));

            Object npcDataWatcher = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(znEntity);

            if (npcIsPlayer)
                ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(entity_id, npcDataWatcher, true));

            if (packetPlayOutScoreboardTeam != null) ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam);
            if (hasToggleHolo) hologram.spawn(player, true);
            if (hasGlow) toggleGlow(glowName, false);

            // Update new npc id.
            entity_id = (Integer) ClassTypes.GET_ENTITY_ID.invoke(znEntity);

            npcEquipments.forEach((itemSlot, material) -> equip(player, itemSlot, material));

            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(getEntity_id(), npcDataWatcher, false));

            viewers.add(player);

            // Fix npc rotation
            lookAt(Optional.of(player), location, true);

            if (npcIsPlayer)
                ServersNPC.getExecutor().execute(() -> hideFromTab(player));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Hides npc on player scoreboard.
     *
     * @param player The player to hide the npc for.
     */
    public void hideFromTab(Player player) {
        try {
            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.REMOVE_PLAYER_FIELD.get(null), Collections.singletonList(znEntity)));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Deletes the npc for player.
     *
     * @param player The player to delete the npc for.
     */
    public void delete(Player player, boolean removeViewer) {
        try {
            if (npcType == NPCType.PLAYER) hideFromTab(player);

            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(new int[]{entity_id}));

            if (removeViewer) viewers.remove(player);
            hologram.delete(player, removeViewer);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Makes the npc look at the location
     *
     * @param location the location to look
     */
    public void lookAt(Optional<Player> playerOptional, Location location, boolean rotation) {
        Location direction = (rotation ? location : this.location.clone().setDirection(location.clone().subtract(this.location.clone()).toVector()));
        boolean rotate = (rotation || hasLookAt);

        try {
            Object lookPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.newInstance(entity_id, (byte) (direction.getYaw() % (rotate ? 360 : 0) * 256 / 360), (byte) (direction.getPitch() % (rotate ? 360. : 0) * 256 / 360), false);
            Object headRotationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.newInstance(znEntity, (byte) ((direction.getYaw() % 360.) * 256 / 360));

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
    public GameProfile getGameProfileForPlayer(Player player) {
        try {
            Object playerProfile = ClassTypes.GET_PROFILE_METHOD.invoke(ClassTypes.GET_HANDLE_PLAYER_METHOD.invoke(player));

            GameProfile newProfile = new GameProfile(UUID.randomUUID(), "znpcs_" + getId());
            newProfile.getProperties().putAll(((GameProfile) playerProfile).getProperties());

            return newProfile;
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Toggles the npc name.
     *
     * @param toggleName Toggle (Hide/Show) the npc name.
     */
    public void toggleName(boolean toggleName) {
        if (toggleName) hasToggleName = !hasToggleName;

        try {
            Object packetPlayOutScoreboardTeam = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR.newInstance();

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, (Utils.versionNewer(9) ? "i" : "h"), (hasToggleName ? 0 : 1));
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", (Utils.versionNewer(13)) ? getHologram().getStringNewestVersion(null, gameProfile.getName()) : gameProfile.getName());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "a", Utils.generateRandomString());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, (Utils.versionNewer(9) ? "j" : "i"), 0);

            if (Utils.versionNewer(9) && hasGlow && glowColor != null) {
                int id = (int) ClassTypes.GET_ENUM_CHAT_ID_METHOD.invoke(glowColor);

                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", id);
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "c", ClassTypes.ENUM_CHAT_TO_STRING_METHOD.invoke(glowColor));
            }

            Collection<String> collection = Collections.singletonList(gameProfile.getName());

            if (Utils.versionNewer(9)) ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "h", collection);
            else ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", collection);

            this.packetPlayOutScoreboardTeam = packetPlayOutScoreboardTeam;
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Deletes NPC for current viewers.
     */
    public void deleteViewers() {
        if (getViewers().isEmpty())
            return;

        Iterator<Player> iterator = getViewers().iterator();
        do {
            delete(iterator.next(), Boolean.FALSE);

            iterator.remove();
        } while (iterator.hasNext());
    }

    /**
     * Updates the new npc location according to current path index.
     *
     * @param pathReader The npc path.
     * @param location The new location.
     */
    public void updatePathLocation(ZNPCPathReader pathReader, Location location) {
        try {
            ClassTypes.SET_LOCATION_METHOD.invoke(znEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            updateLocation();

            if (hologram != null)
                hologram.setLocation(location.clone().subtract(0.5, 0, 0.5), npcType.getHoloHeight());

            int pathIndex = pathReader.getLocationList().indexOf(currentPathLocation);

            Vector vector = (reversePath ? pathReader.getLocationList().get(Math.max(0, Math.min(pathReader.getLocationList().size() - 1, pathIndex + 1))) : pathReader.getLocationList().get(Math.min(pathReader.getLocationList().size() - 1, (Math.max(0, pathIndex - 1))))).toVector();
            double yDiff = (location.getY() - vector.getY());

            Location direction = currentPathLocation.clone().setDirection(location.clone().subtract(vector.clone().add(new Vector(0, yDiff, 0))).clone().toVector());
            lookAt(Optional.empty(), direction, true);
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Resolves the current npc path.
     */
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
     * Sends a npc customization to all viewers.
     *
     * @param name   The Method name.
     * @param values The Method values.
     */
    public void customize(String name, List<?> values) {
        try {
            customizationMap.put(name, values);

            Object customizationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(getEntity_id(), ClassTypes.GET_DATA_WATCHER_METHOD.invoke(getZnEntity()), true);
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, customizationPacket));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @inheritDoc
     */
    public void toggleLookAt() {
        hasLookAt = !hasLookAt;
    }

    /**
     * @inheritDoc
     */
    public void toggleHolo() {
        hasToggleHolo = !hasToggleHolo;

        if (!hasToggleHolo) viewers.forEach(player -> hologram.delete(player, true));
        else viewers.forEach(player -> hologram.spawn(player, true));
    }

    /**
     * @inheritDoc
     */
    public void toggleMirror() {
        hasMirror = !hasMirror;
    }

    /**
     * Sets a new path for the npc.
     *
     * @param pathReader The new path.
     */
    public void setPath(ZNPCPathReader pathReader) {
        npcPath = pathReader;
        pathName = (pathReader != null ? pathReader.getName() : "none");
    }

    /**
     * Gets the current location of the npc.
     *
     * @return The npc location.
     */
    public Location getLocation() {
        return (hasPath() && currentPathLocation != null ? currentPathLocation : location);
    }

    /**
     * Checks if npc has a path.
     *
     * @return {@code true} if the npc has a path.
     */
    public boolean hasPath() {
        return (getPathName() == null || getPathName().equalsIgnoreCase("none"));
    }

    /**
     * Find the enum for a glow color. If no such
     * enum is found, default glow color (@WHITE) will be returned.
     *
     * @param glowColorName The glow color name.
     * @return Glow color enum.
     */
    public Object getGlowColor(String glowColorName) {
        try {
            return ClassTypes.ENUM_CHAT_FORMAT.getField(glowColorName.trim().toUpperCase()).get(null);
        } catch (NoSuchFieldException e) {
            // Get Default Glow-Color
            return getGlowColor("WHITE");
        } catch (IllegalAccessException accessException) {
            throw new AssertionError(accessException);
        }
    }

    /**
     * Initialization of customization for the npc.
     *
     * @param customizationMap The customization values.
     */
    public void setCustomizationMap(HashMap<String, List> customizationMap) {
        this.customizationMap = customizationMap;
        for (Map.Entry<String, List> stringEntry : customizationMap.entrySet()) {
            if (npcType.getCustomizationMethods().containsKey(stringEntry.getKey())) {
                try {
                    npcType.invokeMethod(stringEntry.getKey(), getZnEntity(), NPCType.arrayToPrimitive((String[]) stringEntry.getValue().stream().map(Objects::toString).toArray(String[]::new), npcType.getCustomizationMethods().get(stringEntry.getKey())));
                } catch (Exception e) {
                    // Remove customization from map
                    customizationMap.remove(stringEntry.getKey());
                }
            }
        }
    }
}