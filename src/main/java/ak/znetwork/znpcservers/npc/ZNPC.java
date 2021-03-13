package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.location.ZLocation;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
import ak.znetwork.znpcservers.npc.path.ZNPCPathReader;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;
import ak.znetwork.znpcservers.npc.skin.ZNPCSkin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

import static ak.znetwork.znpcservers.cache.impl.ClassCacheImpl.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter @Setter
public class ZNPC {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Bukkit.getLogger();

    /**
     * Determines if v1.9+ methods will be used.
     */
    private static final boolean V9 = Utils.versionNewer(9);

    /**
     * The default path name when no path is found.
     */
    private static final String DEFAULT_PATH = "none";

    /**
     * The profile textures in the npc {@link GameProfile}.
     */
    private static final String PROFILE_TEXTURES = "textures";

    /**
     * The team name in the npc scoreboard.
     */
    private static final String TEAM_NAME = "a";

    /**
     * The team mode id in the npc scoreboard.
     */
    private static final String TEAM_MODE = V9 ? "i" : "h";

    /**
     * The default glow color.
     */
    private static final String GLOW_COLOR = "WHITE";

    /**
     * The npc identifier.
     */
    @Expose
    private int id;

    /**
     * Toggle variables.
     */
    @Expose
    private boolean hasGlow, hasToggleName, hasLookAt, hasMirror, isReversePath = false;

    /**
     * Toggle variables.
     */
    @Expose
    private boolean hasToggleHolo = true;

    /**
     * Determines if the npc should be saved.
     */
    @Expose
    private boolean save;

    /**
     * The skin value & signature.
     */
    @Expose
    private String skin, signature;

    /**
     * The hologram lines.
     */
    @Expose
    private String lines;

    /**
     * The path name.
     */
    @Expose
    private String pathName;

    /**
     * The glow name.
     */
    @Expose
    private String glowName = GLOW_COLOR;

    /**
     * The npc location
     */
    @Expose
    private ZLocation location;

    /**
     * The npc entity type.
     */
    @Expose
    private NPCType npcType;

    /**
     * The actions to be executed when the npc is clicked.
     */
    @Expose
    private List<String> actions = new ArrayList<>();

    /**
     * The npc equipment.
     */
    @Expose
    private HashMap<NPCItemSlot, Material> npcEquipments = new HashMap<>();

    /**
     * The npc customizations.
     */
    @Expose
    private HashMap<String, String[]> customizationMap = new HashMap<>();

    /**
     * The npc entity id.
     */
    private int entityId;

    /**
     * The npc name.
     */
    private String npcName;

    /**
     * The current world for the npc.
     */
    private String worldName;

    /**
     * The npc hologram.
     */
    private Hologram hologram;

    /**
     * A list of players the players who can see the npc.
     */
    private HashSet<Player> viewers;

    /**
     * Cache reflection variables.
     */
    private Object glowColor,dataWatcherRegistryEnum,tabConstructor,znEntity;

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
    private boolean pathReverse = false;

    /**
     * Determines if the npc is created by first time.
     */
    private boolean setup = false;

    /**
     * Creates a new NPC.
     *
     * @param id            The npc id.
     * @param lines         The hologram lines.
     * @param location      The npc location.
     * @param npcType       The npc entity type.
     */
    public ZNPC(int id,
                String lines,
                ZNPCSkin znpcSkin,
                ZLocation location,
                NPCType npcType) {
        this.id = id;
        this.lines = lines;
        this.skin = znpcSkin.getValue();
        this.signature = znpcSkin.getSignature();
        this.location = location;
        this.npcType = npcType;

        this.init();
    }

    /**
     * Initialization of all necessary functions for the npc.
     * Called when a npc is created for the first time.
     */
    public void init() {
        this.viewers = new HashSet<>();

        this.setNpcName("zNPC_" + getId());

        this.gameProfile = new GameProfile(UUID.randomUUID(), getNpcName());
        this.gameProfile.getProperties().put(PROFILE_TEXTURES, new Property(PROFILE_TEXTURES, skin, signature));

        this.changeType(getNpcType());
        this.loadCustomization();

        if (getPathName() != null)
            this.setPath(ZNPCPathReader.find(getPathName()));
    }

    /**
     * Set/Update the npc glow.
     *
     * @param color  The glow color.
     * @param toggle Toggles (on/off) the glow of the npc.
     */
    public void toggleGlow(String color, boolean toggle) {
        if (!V9)
            throw new UnsupportedOperationException("Version not supported");

        if (toggle)
            setHasGlow(!isHasGlow());

        try {
            Object npcDataWatcher = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(getZnEntity());
            ClassTypes.SET_DATA_WATCHER_METHOD.invoke(npcDataWatcher, ClassTypes.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(0, dataWatcherRegistryEnum), (isHasGlow() ? (byte) 0x40 : (byte) 0x0));

            Object packet = ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(getEntityId(), npcDataWatcher, true);
            getViewers().forEach(player -> ReflectionUtils.sendPacket(player, packet));

            setGlowColor(getGlowColor(color));
            setGlowName(color);

            // Update new glow color
            getViewers().forEach(this::toggleName);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Updates the npc location.
     */
    public void updateLocation() {
        try {
            Object npcTeleportPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(getZnEntity());
            getViewers().forEach(player -> ReflectionUtils.sendPacket(player, npcTeleportPacket));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Sets a new location for the npc.
     *
     * @param location The new location.
     */
    public void setLocation(Location location) {
        try {
            if (!hasPath()) {
                this.location = new ZLocation(location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5, location.getYaw(), location.getPitch()));

                lookAt(null, location, true);
            }

            ClassTypes.SET_LOCATION_METHOD.invoke(getZnEntity(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            updateLocation();

            getHologram().setLocation(location, getNpcType().getHoloHeight());
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
            if (!V9) {
                equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD.newInstance(getEntityId(), slot.getSlotOld(), item);
            } else {
                if (Utils.versionNewer(16)) {
                    List<Pair<?, ?>> pairs = (List<Pair<?, ?>>) ReflectionUtils.getValue(ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT.newInstance(), "b");
                    pairs.add(new Pair<>(ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[slot.getSlotNew()], item));

                    equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEW.newInstance(getEntityId(), pairs);
                } else {
                    equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD.newInstance(getEntityId(), ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[slot.getSlotNew()], item);
                }
            }

            getNpcEquipments().put(slot, material);

            if (player != null) ReflectionUtils.sendPacket(player, equipPacket);
            else getViewers().forEach(players -> ReflectionUtils.sendPacket(players, equipPacket));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Updates the npc skin.
     *
     * @param skinFetch The skin to set.
     */
    public void changeSkin(ZNPCSkin skinFetch) {
        setSkin(skinFetch.getValue());
        setSignature(skinFetch.getSignature());

        getGameProfile().getProperties().clear();
        getGameProfile().getProperties().put(PROFILE_TEXTURES, new Property(PROFILE_TEXTURES, getSkin(), getSignature()));

        // Update new game profile properties
        updateProfile(getGameProfile().getProperties());

        // Spawn npc again for viewers
        deleteViewers();
    }

    /**
     * Enables second layer of skin for the npc.
     */
    public void setSecondLayerSkin() {
        try {
            Object dataWatcherObject = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(getZnEntity());

            if (V9) {
                dataWatcherRegistryEnum = ClassTypes.DATA_WATCHER_REGISTER_ENUM_FIELD.get(null);

                ClassTypes.SET_DATA_WATCHER_METHOD.invoke(dataWatcherObject, ClassTypes.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(Utils.versionNewer(16) ? 16 : Utils.BUKKIT_VERSION <= 13 ? 13 : 15, dataWatcherRegistryEnum), (byte) 127);
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
        if (isSetup() && getNpcType() == npcType)
            return;

        try {
            Object nmsWorld = ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(getLocation().getWorld());
            setZnEntity(npcType == NPCType.PLAYER ? ClassTypes.PLAYER_CONSTRUCTOR.newInstance(ClassTypes.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, getGameProfile(), (Utils.versionNewer(14) ? ClassTypes.PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR : ClassTypes.PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR).newInstance(nmsWorld)) : (Utils.versionNewer(14) ? npcType.getConstructor().newInstance(npcType.getEntityType(), nmsWorld) : npcType.getConstructor().newInstance(nmsWorld)));

            if (npcType == NPCType.PLAYER) {
                setTabConstructor(ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.ADD_PLAYER_FIELD.get(null), Collections.singletonList(getZnEntity())));

                // Fix second layer skin for entity player
                setSecondLayerSkin();
            }

            setNpcType(npcType);

            setLocation(getLocation());

            // Update new type for viewers
            deleteViewers();

            // Update new entity id
            setEntityId((Integer) ClassTypes.GET_ENTITY_ID.invoke(getZnEntity()));

            // Check if the npc is created by first time
            if (!isSetup())
                setSetup(true);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Spawns the npc for a player.
     *
     * @param player The player to see the npc.
     */
    public void spawn(Player player) {
        // Update the npc scoreboard for player
        toggleName(player);

        try {
            // Check if npc type is player
            boolean npcIsPlayer = getNpcType() == NPCType.PLAYER;

            if (npcIsPlayer && isHasMirror()) {
                // Set npc skin to player skin
                updateProfile(getGameProfileForPlayer(player).getProperties());
            }

            if (npcIsPlayer) ReflectionUtils.sendPacket(player, getTabConstructor());
            ReflectionUtils.sendPacket(player, npcIsPlayer ? ClassTypes.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.newInstance(getZnEntity()) : ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(getZnEntity()));

            Object npcDataWatcher = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(getZnEntity());

            if (npcIsPlayer)
                ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(getEntityId(), npcDataWatcher, true));

            if (isHasToggleHolo())
                getHologram().spawn(player);
            if (isHasGlow() && V9)
                toggleGlow(getGlowName(), false);

            // Update new npc id
            setEntityId((Integer) ClassTypes.GET_ENTITY_ID.invoke(getZnEntity()));

            // Send npc equipment packets for player
            getNpcEquipments().forEach((itemSlot, material) -> equip(player, itemSlot, material));

            // Update npc data
            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(getEntityId(), npcDataWatcher, true));

            // Add player to viewers list
            getViewers().add(player);

            // Fix npc rotation
            lookAt(player, location.toBukkitLocation(), true);

            if (npcIsPlayer)
                ServersNPC.SCHEDULER.scheduleSyncDelayedTask(() ->
                        hideFromTab(player),
                        60
                );
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Hides npc on player tab list.
     *
     * @param player The player to hide the npc for.
     */
    public void hideFromTab(Player player) {
        try {
            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.REMOVE_PLAYER_FIELD.get(null), Collections.singletonList(getZnEntity())));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Deletes the npc scoreboard for player.
     *
     * @param player The player to delete the scoreboard for.
     */
    public void deleteScoreboard(Player player) {
        try {
            Object packetPlayOutScoreboardTeam = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR.newInstance();

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_NAME, getGameProfile().getName());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_MODE, 1);

            ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam);
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException | InvocationTargetException operationException) {
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
            if (getNpcType() == NPCType.PLAYER)
                hideFromTab(player);

            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(new int[]{getEntityId()}));
            getHologram().delete(player);

            if (removeViewer)
                getViewers().remove(player);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Makes the npc look at the location.
     *
     * @param location The location to look.
     */
    public void lookAt(Player player, Location location, boolean rotation) {
        Location direction = (rotation ? location : this.location.toBukkitLocation().clone().setDirection(location.clone().subtract(this.location.toBukkitLocation().clone()).toVector()));

        try {
            Object lookPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.newInstance(getEntityId(), (byte) (direction.getYaw() * 256.0F / 360.0F), (byte) (direction.getPitch() * 256.0F / 360.0F), true);
            Object headRotationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.newInstance(getZnEntity(), (byte) (direction.getYaw() * 256.0F / 360.0F));

            if (player != null) ReflectionUtils.sendPacket(player, lookPacket, headRotationPacket);
            else getViewers().forEach(players -> ReflectionUtils.sendPacket(players, headRotationPacket));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Gets the game-profile for player.
     *
     * @param player The player.
     * @return       The player game-profile or default npc game-profile if not found.
     */
    public GameProfile getGameProfileForPlayer(Player player) {
        return NPCManager.getNpcUsers().stream().filter(npcUser -> npcUser.getUuid().equals(player.getUniqueId())).map(ZNPCUser::getGameProfile).findFirst().orElse(getGameProfile());
    }

    /**
     * Toggles the npc name.
     */
    public void toggleName(Player player) {
        deleteScoreboard(player);

        try {
            Object packetPlayOutScoreboardTeam = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR.newInstance();

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_NAME, getGameProfile().getName());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", Utils.versionNewer(13) ? getHologram().getStringNewestVersion(null, getGameProfile().getName()) : getGameProfile().getName());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_MODE, 0);
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, V9 ? "j" : "i", 0);

            if (V9 && isHasGlow() && getGlowColor() != null) {
                Object enumPrefix = ClassTypes.ENUM_CHAT_TO_STRING_METHOD.invoke(getGlowColor());

                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", Utils.versionNewer(13) ? getGlowColor() : ClassTypes.GET_ENUM_CHAT_ID_METHOD.invoke(getGlowColor()));
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "c", Utils.versionNewer(13) ? ClassTypes.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance(enumPrefix) : enumPrefix);
            }

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, V9 ? "h" : "g", Collections.singletonList(getGameProfile().getName()));

            ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam);
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
     * @param location   The npc path location.
     */
    public void updatePathLocation(ZNPCPathReader pathReader, Location location) {
        int pathIndex = pathReader.getLocationList().indexOf(getCurrentPathLocation());

        Vector vector = (isPathReverse() ? pathReader.getLocationList().get(Math.max(0, Math.min(pathReader.getLocationList().size() - 1, pathIndex + 1))) : pathReader.getLocationList().get(Math.min(pathReader.getLocationList().size() - 1, (Math.max(0, pathIndex - 1))))).toVector();
        double yDiff = (location.getY() - vector.getY());

        Location direction = getCurrentPathLocation().clone().setDirection(location.clone().subtract(vector.clone().add(new Vector(0, yDiff, 0))).toVector());

        setLocation(direction);
        lookAt(null, direction, true);
    }

    /**
     * Updates the npc game-profile.
     */
    public void updateProfile(PropertyMap propertyMap) {
        try {
            Object gameProfileObj = ClassTypes.GET_PROFILE_METHOD.invoke(getZnEntity());

            ReflectionUtils.setValue(gameProfileObj, "id", UUID.randomUUID());
            ReflectionUtils.setValue(gameProfileObj, "properties", propertyMap);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Resolves the current npc path.
     */
    public void handlePath() {
        if (getNpcPath() == null)
            return;

        if (isReversePath()) {
            if (getCurrentEntryPath() <= 0) setPathReverse(false);
            else if (getCurrentEntryPath() >= getNpcPath().getLocationList().size() - 1) setPathReverse(true);
        }

        setCurrentPathLocation(getNpcPath().getLocationList().get(Math.min(getNpcPath().getLocationList().size() - 1, getCurrentEntryPath())));

        if (!isPathReverse()) setCurrentEntryPath(getCurrentEntryPath() + 1);
        else setCurrentEntryPath(getCurrentEntryPath() - 1);

        updatePathLocation(getNpcPath(), getCurrentPathLocation());
    }

    /**
     * Sends a npc customization to all viewers.
     *
     * @param name   The Method name.
     * @param values The Method values.
     */
    public void customize(String name, String[] values) {
        try {
            getCustomizationMap().put(name, values);

            Object customizationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(getEntityId(), ClassTypes.GET_DATA_WATCHER_METHOD.invoke(getZnEntity()), true);
            getViewers().forEach(player -> ReflectionUtils.sendPacket(player, customizationPacket));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * @inheritDoc
     */
    public void toggleLookAt() {
        setHasLookAt(!isHasLookAt());
    }

    /**
     * @inheritDoc
     */
    public void toggleHolo() {
        setHasToggleHolo(!isHasToggleHolo());

        if (!isHasToggleHolo()) getViewers().forEach(player -> getHologram().delete(player));
        else getViewers().forEach(player -> getHologram().spawn(player));
    }

    /**
     * @inheritDoc
     */
    public void toggleMirror() {
        setHasMirror(!isHasMirror());
    }

    /**
     * Sets a new path for the npc.
     *
     * @param pathReader The new path.
     */
    public void setPath(ZNPCPathReader pathReader) {
        setNpcPath(pathReader);
        setPathName(pathReader != null ? pathReader.getName() : DEFAULT_PATH);
    }

    /**
     * Gets the current location of the npc.
     *
     * @return The npc location.
     */
    public Location getLocation() {
        return hasPath() && getCurrentPathLocation() != null ?
                getCurrentPathLocation() : location.toBukkitLocation();
    }

    /**
     * Gets the npc hologram or create a new one if not found.
     *
     * @return The npc hologram.
     */
    public Hologram getHologram() {
        return hologram == null ? (hologram = new Hologram(this, location.toBukkitLocation())) : hologram;
    }

    /**
     * Checks if npc has a path.
     *
     * @return {@code true} If the npc has a path.
     */
    public boolean hasPath() {
        return getPathName() != null && !getPathName().equalsIgnoreCase(DEFAULT_PATH);
    }

    /**
     * Find the enum for a glow color. If no such
     * enum is found, default glow color (@WHITE) will be returned.
     *
     * @param glowColorName The glow color name.
     * @return              The glow color enum.
     */
    public Object getGlowColor(String glowColorName) {
        try {
            return ClassCache.find(glowColorName, ClassTypes.ENUM_CHAT_CLASS);
        } catch (NullPointerException e) {
            // Return default glow-color
            return getGlowColor(GLOW_COLOR);
        }
    }

    /**
     * Used to properly save a text in database.
     *
     * @return The hologram lines formatted.
     */
    public String getTextFormatted(String... text) {
        return String.join(":", text);
    }

    /**
     * Loads customization for the npc.
     */
    private void loadCustomization() {
        for (Map.Entry<String, String[]> entry : getCustomizationMap().entrySet()) {
            if (getNpcType().getCustomizationMethods().containsKey(entry.getKey())) {
                try {
                    getNpcType().invokeMethod(entry.getKey(), getZnEntity(), NPCType.arrayToPrimitive(entry.getValue(), getNpcType().getCustomizationMethods().get(entry.getKey())));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, String.format("Skipping customization (%s) for npc " + getId(), entry.getKey()));
                }
            }
        }
    }
}