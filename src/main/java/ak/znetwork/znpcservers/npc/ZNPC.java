package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.npc.model.ZNPCPojo;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.location.ZLocation;
import ak.znetwork.znpcservers.npc.enums.NPCItemSlot;
import ak.znetwork.znpcservers.npc.enums.NPCType;
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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map.Entry;

import lombok.Getter;

import static ak.znetwork.znpcservers.cache.impl.ClassCacheImpl.*;
import static ak.znetwork.znpcservers.npc.path.ZNPCPathImpl.*;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class ZNPC {

    /**
     * A map containing the stored npcs.
     */
    private static final ConcurrentMap<Integer, ZNPC> NPC_MAP = new ConcurrentHashMap<>();

    /**
     * The logger.
     */
    private static final Logger LOGGER = Bukkit.getLogger();

    /**
     * The profile textures for the npc {@link GameProfile}.
     */
    private static final String PROFILE_TEXTURES = "textures";

    /**
     * The team name in the npc scoreboard.
     */
    private static final String TEAM_NAME = "a";

    /**
     * The team mode id in the npc scoreboard.
     */
    private static final String TEAM_MODE = Utils.versionNewer(9) ? "i" : "h";

    /**
     * The npc pojo.
     */
    private final ZNPCPojo npcPojo;

    /**
     * The npc hologram.
     */
    private final Hologram hologram;

    /**
     * A set of players who can see the npc.
     */
    private final HashSet<Player> viewers;

    /**
     * The bukkit entity id.
     */
    private int entityID;

    /**
     * Cache reflection variables.
     */
    private Object glowColor, dataWatcherRegistryEnum, tabConstructor, nmsEntity, bukkitEntity;

    /**
     * The current profile of the npc.
     */
    private GameProfile gameProfile;

    /**
     * The npc path.
     */
    private ZNPCPath npcPath;

    /**
     * Creates a new NPC.
     *
     * @param npcModel The npc model.
     */
    public ZNPC(ZNPCPojo npcModel) {
        this.npcPojo = npcModel;
        viewers = new HashSet<>();
        hologram = new Hologram(this);
        init();
    }

    /**
     * Initialization of all necessary functions for the npc.
     * Called when a npc is created for the first time.
     */
    public void init() {
        // Register NPC in map
        NPC_MAP.put(getNpcPojo().getId(), this);

        this.gameProfile = new GameProfile(UUID.randomUUID(), "zNPC_" + npcPojo.getId());
        this.gameProfile.getProperties().put(PROFILE_TEXTURES, new Property(PROFILE_TEXTURES, npcPojo.getSkin(), npcPojo.getSignature()));

        this.changeType(npcPojo.getNpcType());
        this.loadCustomization();

        if (npcPojo.getPathName() != null) {
            this.setPath(AbstractTypeWriter.find(npcPojo.getPathName()));
        }
    }

    /**
     * Sets / Updates the npc glow.
     *
     * @param color The npc glow color.
     */
    public void toggleGlow(String color) {
        if (!Utils.versionNewer(9)) {
            throw new UnsupportedOperationException("Version not supported");
        }

        try {
            Object npcDataWatcher = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(nmsEntity);
            ClassTypes.SET_DATA_WATCHER_METHOD.invoke(npcDataWatcher, ClassTypes.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(0, dataWatcherRegistryEnum), (npcPojo.isHasGlow() ? (byte) 0x40 : (byte) 0x0));

            // Update npc data
            updateMetaData();

            glowColor = getGlowColor(color);
            npcPojo.setGlowName(color);

            // Update new glow color for current npc viewers
            viewers.forEach(this::updateScoreboard);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Updates the npc location for current viewers.
     */
    public void updateLocation() {
        try {
            Object npcTeleportPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(nmsEntity);
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, npcTeleportPacket));
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
            if (getNpcPath() == null) {
                npcPojo.setLocation(new ZLocation(location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY(), location.getBlockZ() + 0.5, location.getYaw(), location.getPitch())));

                lookAt(null, location, true);
            }

            ClassTypes.SET_LOCATION_METHOD.invoke(nmsEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            updateLocation();

            hologram.setLocation(location, npcPojo.getNpcType().getHoloHeight());
        } catch (IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Equip the npc.
     *
     * @param player    The player to send the new npc equipment.
     * @param slot      The item slot.
     * @param itemStack The item to equip.
     */
    public void equip(Player player, NPCItemSlot slot, ItemStack itemStack) {
        try {
            Object item = ClassTypes.AS_NMS_COPY_METHOD.invoke(ClassTypes.CRAFT_ITEM_STACK_CLASS, itemStack);

            Object equipPacket;
            if (!Utils.versionNewer(9)) {
                equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_OLD.newInstance(entityID, slot.getSlotOld(), item);
            } else {
                if (Utils.versionNewer(16)) {
                    List<Pair<?, ?>> pairs = (List<Pair<?, ?>>) ReflectionUtils.getValue(ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT.newInstance(), "b");
                    pairs.add(new Pair<>(ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[slot.getSlotNew()], item));

                    equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEW.newInstance(entityID, pairs);
                } else {
                    equipPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_EQUIPMENT_CONSTRUCTOR_NEWEST_OLD.newInstance(entityID, ClassTypes.ENUM_ITEM_SLOT.getEnumConstants()[slot.getSlotNew()], item);
                }
            }

            if (player != null) ReflectionUtils.sendPacket(player, equipPacket);
            else viewers.forEach(players -> ReflectionUtils.sendPacket(players, equipPacket));
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
        npcPojo.setSkin(skinFetch.getValue());
        npcPojo.setSignature(skinFetch.getSignature());

        gameProfile.getProperties().clear();
        gameProfile.getProperties().put(PROFILE_TEXTURES, new Property(PROFILE_TEXTURES, npcPojo.getSkin(), npcPojo.getSignature()));

        // Update new game profile properties
        updateProfile(gameProfile.getProperties());

        // Spawn npc again for viewers
        deleteViewers();
    }

    /**
     * Enables second layer of skin for the npc.
     */
    public void setSecondLayerSkin() {
        try {
            Object dataWatcherObject = ClassTypes.GET_DATA_WATCHER_METHOD.invoke(nmsEntity);

            if (Utils.versionNewer(9)) {
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
        try {
            Object nmsWorld = ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(getLocation().getWorld());

            nmsEntity = (npcType == NPCType.PLAYER ? ClassTypes.PLAYER_CONSTRUCTOR.newInstance(ClassTypes.GET_SERVER_METHOD.invoke(Bukkit.getServer()), nmsWorld, gameProfile, (Utils.versionNewer(14) ? ClassTypes.PLAYER_INTERACT_MANAGER_NEW_CONSTRUCTOR : ClassTypes.PLAYER_INTERACT_MANAGER_OLD_CONSTRUCTOR).newInstance(nmsWorld)) : (Utils.versionNewer(14) ? npcType.getConstructor().newInstance(npcType.getEntityType(), nmsWorld) : npcType.getConstructor().newInstance(nmsWorld)));
            bukkitEntity = (ClassTypes.GET_BUKKIT_ENTITY_METHOD.invoke(nmsEntity));

            if (npcType == NPCType.PLAYER) {
                tabConstructor = (ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.ADD_PLAYER_FIELD.get(null), Collections.singletonList(nmsEntity)));

                // Fix second layer skin for entity player
                setSecondLayerSkin();
            }

            npcPojo.setNpcType(npcType);

            setLocation(getLocation());

            // Update new type for viewers
            deleteViewers();

            // Update new entity id
            entityID = ((Integer) ClassTypes.GET_ENTITY_ID.invoke(nmsEntity));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Spawns the npc for the given player.
     *
     * @param player The player to spawn the npc.
     */
    public void spawn(Player player) {
        // Update the npc scoreboard for player
        updateScoreboard(player);

        try {
            // Check if npc type is player
            boolean npcIsPlayer = npcPojo.getNpcType() == NPCType.PLAYER;

            if (npcIsPlayer) {
                if (npcPojo.isHasMirror()) {
                    // Set npc skin to player skin
                    updateProfile(getGameProfileForPlayer(player).getProperties());
                }

                ReflectionUtils.sendPacket(player, tabConstructor);
            }

            ReflectionUtils.sendPacket(player, npcIsPlayer ? ClassTypes.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.newInstance(nmsEntity) : ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(nmsEntity));

            if (npcPojo.isHasToggleHolo())
                hologram.spawn(player);

            if (npcPojo.isHasGlow() && Utils.versionNewer(9))
                toggleGlow(npcPojo.getGlowName());

            // Update the npc bukkit id
            entityID = ((Integer) ClassTypes.GET_ENTITY_ID.invoke(nmsEntity));

            // Update npc data
            updateMetaData();

            // Add player to viewers list
            viewers.add(player);

            // Update npc equipment
            updateEquipment();

            // Fix npc rotation
            lookAt(player, npcPojo.getLocation().toBukkitLocation(), true);

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
     * Hides the npc on tab-list for the given player.
     *
     * @param player The player to hide the npc for.
     */
    public void hideFromTab(Player player) {
        try {
            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.REMOVE_PLAYER_FIELD.get(null), Collections.singletonList(nmsEntity)));
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Deletes the npc scoreboard for the given player.
     *
     * @param player The player to delete the scoreboard for.
     */
    public void deleteScoreboard(Player player) {
        try {
            Object packetPlayOutScoreboardTeam = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR.newInstance();

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_NAME, gameProfile.getName());
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_MODE, 1);

            ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam);
        } catch (IllegalAccessException | NoSuchFieldException | InstantiationException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Deletes the npc for the given player.
     *
     * @param player The player to delete the npc for.
     */
    public void delete(Player player, boolean removeViewer) {
        if (!viewers.contains(player))
            return;

        try {
            if (npcPojo.getNpcType() == NPCType.PLAYER)
                hideFromTab(player);

            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_ENTITY_DESTROY_CONSTRUCTOR.newInstance(new int[]{entityID}));
            hologram.delete(player);

            if (removeViewer) {
                viewers.remove(player);
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Makes the npc look at a location.
     *
     * @param location The location to look.
     */
    public void lookAt(Player player, Location location, boolean rotation) {
        Location direction = (rotation ? location : npcPojo.getLocation().toBukkitLocation().clone().setDirection(location.clone().subtract(npcPojo.getLocation().toBukkitLocation().clone()).toVector()));

        try {
            Object lookPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.newInstance(entityID, (byte) (direction.getYaw() * 256.0F / 360.0F), (byte) (direction.getPitch() * 256.0F / 360.0F), true);
            Object headRotationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.newInstance(nmsEntity, (byte) (direction.getYaw() * 256.0F / 360.0F));

            if (player != null) {
                ReflectionUtils.sendPacket(player, lookPacket, headRotationPacket);
            } else {
                viewers.forEach(players -> ReflectionUtils.sendPacket(players, headRotationPacket));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Returns the game-profile for the given player.
     *
     * @param player The player.
     * @return The player game-profile.
     */
    public GameProfile getGameProfileForPlayer(Player player) {
        return ZNPCUser.registerOrGet(player).getGameProfile();
    }

    /**
     * Updates the npc scoreboard team for the given player.
     */
    public void updateScoreboard(Player player) {
        deleteScoreboard(player);

        try {
            final boolean V9 = Utils.versionNewer(9);
            final boolean V13 = Utils.versionNewer(13);

            final String profileName = gameProfile.getName();

            Object packetPlayOutScoreboardTeam = ClassTypes.PACKET_PLAY_OUT_SCOREBOARD_TEAM_CONSTRUCTOR.newInstance();

            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_NAME, profileName);
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "b", V13 ? hologram.getStringNewestVersion(null, profileName) : profileName);
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "e", "never");
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, TEAM_MODE, 0);
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, V9 ? "j" : "i", 0);
            ReflectionUtils.setValue(packetPlayOutScoreboardTeam, V9 ? "h" : "g", Collections.singletonList(profileName));

            if (V9 && npcPojo.isHasGlow() && glowColor != null) {
                Object enumPrefix = ClassTypes.ENUM_CHAT_TO_STRING_METHOD.invoke(glowColor);

                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "g", V13 ? glowColor : ClassTypes.GET_ENUM_CHAT_ID_METHOD.invoke(glowColor));
                ReflectionUtils.setValue(packetPlayOutScoreboardTeam, "c", V13 ? ClassTypes.I_CHAT_BASE_COMPONENT_A_CONSTRUCTOR.newInstance(enumPrefix) : enumPrefix);
            }

            ReflectionUtils.sendPacket(player, packetPlayOutScoreboardTeam);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    /**
     * Updates the npc equipment for current viewers.
     */
    public void updateEquipment() {
        for (Entry<NPCItemSlot, ItemStack> items : getNpcPojo().getNpcEquip().entrySet()) {
            viewers.forEach(player -> equip(player, items.getKey(), items.getValue()));
        }
    }

    /**
     * Deletes the NPC for current viewers.
     */
    public void deleteViewers() {
        if (viewers.isEmpty())
            return;

        Iterator<Player> iterator = viewers.iterator();
        do {
            delete(iterator.next(), Boolean.FALSE);
            iterator.remove();
        } while (iterator.hasNext());
    }

    /**
     * Updates the npc game-profile.
     */
    public void updateProfile(PropertyMap propertyMap) {
        try {
            Object gameProfileObj = ClassTypes.GET_PROFILE_METHOD.invoke(nmsEntity);

            ReflectionUtils.setValue(gameProfileObj, "id", UUID.randomUUID());
            ReflectionUtils.setValue(gameProfileObj, "properties", propertyMap);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException operationException) {
            throw new AssertionError(operationException);
        }
    }

    public void updateMetaData() {
        try {
            Object customizationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(entityID, ClassTypes.GET_DATA_WATCHER_METHOD.invoke(nmsEntity), true);
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, customizationPacket));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Sets a new path for the npc.
     *
     * @param typeWriter The new path.
     */
    public void setPath(AbstractTypeWriter typeWriter) {
        if (typeWriter == null) {
            npcPath = null;
            npcPojo.setPathName("none");
        } else {
            npcPath = typeWriter.getPath(this);
            npcPojo.setPathName(typeWriter.getName());
        }
    }

    /**
     * Returns the current location of the npc.
     *
     * @return The npc location.
     */
    public Location getLocation() {
        return getNpcPath() != null ? getNpcPath().getLocation().toBukkitLocation() : npcPojo.getLocation().toBukkitLocation();
    }

    /**
     * Locates the enum for the given color name, if no such
     * enum is found, default glow color (@WHITE) will be returned.
     *
     * @param glowName The glow color name.
     * @return The glow color enum.
     */
    public Object getGlowColor(String glowName) {
        try {
            return ClassCache.find(glowName, ClassTypes.ENUM_CHAT_CLASS);
        } catch (NullPointerException e) {
            // Return default glow-color
            return getGlowColor("WHITE");
        }
    }

    /**
     * Loads the customization for the npc.
     */
    protected void loadCustomization() {
        for (Map.Entry<String, String[]> entry : npcPojo.getCustomizationMap().entrySet()) {
            String methodName = entry.getKey();
            try {
                npcPojo.getNpcType().updateCustomization(
                        this, methodName, entry.getValue()
                );
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.log(Level.WARNING, String.format("Skipping customization (%s) for npc " + npcPojo.getId(), methodName));
            }
        }
    }

    /**
     * Locates a npc for the given id.
     *
     * @param id The npc id.
     * @return The npc.
     */
    public static ZNPC find(int id) {
        return NPC_MAP.get(id);
    }

    /**
     * Unregisters a npc.
     *
     * @param id The npc id.
     */
    public static void unregister(int id) {
        ZNPC znpc = find(id);
        if (znpc == null)
            return;

        NPC_MAP.remove(id);
        znpc.deleteViewers();
    }

    /**
     * Returns the NPCs in the map.
     *
     * @return The NPCs in the map.
     */
    public static Collection<ZNPC> all() {
        return NPC_MAP.values();
    }
}