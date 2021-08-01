package ak.znetwork.znpcservers.npc;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.hologram.Hologram;
import ak.znetwork.znpcservers.UnexpectedCallException;
import ak.znetwork.znpcservers.npc.model.ZNPCPojo;
import ak.znetwork.znpcservers.npc.packets.list.Packets;
import ak.znetwork.znpcservers.user.ZNPCUser;
import ak.znetwork.znpcservers.utility.location.ZLocation;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;

import static ak.znetwork.znpcservers.npc.ZNPCPath.*;

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
    private static final Logger LOGGER = Logger.getLogger(ZNPC.class.getName());

    /**
     * The profile textures for the npc {@link GameProfile}.
     */
    private static final String PROFILE_TEXTURES = "textures";

    /**
     * The team mode id in the npc scoreboard.
     */
    private static final String TEAM_MODE = Utils.versionNewer(9) ? "i" : "h";

    /**
     * A string whitespace.
     */
    private static final String WHITESPACE = " ";

    /**
     * Tab-list prefix for npc.
     */
    private static final String START_PREFIX = ChatColor.DARK_GRAY + "[ZNPC]" + WHITESPACE;

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
     * The npc tab-name.
     */
    private final String npcName;

    /**
     * Current npc skin.
     */
    private final ZNPCSkin znpcSkin;

    /**
     * Last npc move.
     */
    private long lastMove = -1;

    /**
     * Packets by version.
     */
    private Packets packets;

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
    private PathInitializer npcPath;

    /**
     * Creates a new NPC.
     *
     * @param npcModel The npc model.
     */
    public ZNPC(ZNPCPojo npcModel) {
        this.npcPojo = npcModel;
        viewers = new HashSet<>();
        hologram = new Hologram(this);
        npcName = NamingType.DEFAULT.resolve(this);
        znpcSkin = ZNPCSkin.forValues(npcModel.getSkin(), npcModel.getSignature());
        init();
    }

    /**
     * Initialization of all necessary functions for the npc.
     * Called when a npc is created for the first time.
     */
    public void init() {
        // Register NPC in map
        NPC_MAP.put(getNpcPojo().getId(), this);

        this.gameProfile = new GameProfile(UUID.randomUUID(), START_PREFIX + npcName);
        this.gameProfile.getProperties().put(PROFILE_TEXTURES, new Property(PROFILE_TEXTURES, npcPojo.getSkin(), npcPojo.getSignature()));

        this.packets = Packets.getByVersion(this, Utils.BUKKIT_VERSION); // ....

        this.changeType(npcPojo.getNpcType());
        this.updateProfile(gameProfile.getProperties());

        this.setLocation(getNpcPojo().getLocation().toBukkitLocation());

        if (npcPojo.getPathName() != null) {
            this.setPath(AbstractTypeWriter.find(npcPojo.getPathName()));
        }

        this.npcPojo.getNpcEquip().forEach(this::updateEquipment);

        // Load npc customizations
        for (Map.Entry<String, String[]> entry : npcPojo.getCustomizationMap().entrySet()) {
            try {
                npcPojo.getNpcType().updateCustomization(this, entry.getKey(), entry.getValue());
            } catch (ReflectiveOperationException operationException) {
                LOGGER.log(Level.WARNING, String.format("Skipping customization (%s) for npc " + npcPojo.getId(), entry.getKey()));
            }
        }
    }

    /**
     * Sets / Updates the npc glow.
     *
     * @param color The npc glow color NAME.
     */
    public void toggleGlow(String color) {
        // Only +v1.9 versions support glow
        if (!Utils.versionNewer(9)) {
            throw new UnsupportedOperationException("Version not supported.");
        }

        try {
            // Find glow color enum
            glowColor = ClassTypes.ENUM_CHAT_FORMAT_FIND.invoke(null, color == null ? "WHITE" : color);
            // Update new npc glowColor
            ClassTypes.SET_DATA_WATCHER_METHOD.invoke(ClassTypes.GET_DATA_WATCHER_METHOD.invoke(nmsEntity),
                    ClassTypes.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(0, dataWatcherRegistryEnum), (npcPojo.isHasGlow() ? (byte) 0x40 : (byte) 0x0));
            npcPojo.setGlowName(color);
            // Update glow scoreboard packets
            packets.update();
            // Update npc meta-data
            updateMetaData();
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Updates the npc location for current viewers.
     */
    public void updateLocation() {
        try {
            Object npcTeleportPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(nmsEntity);
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, npcTeleportPacket));
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Sets a new location for the npc.
     *
     * @param location The new location.
     */
    public void setLocation(Location location) {
        try {
            // If the npc has a path it will not look at the players or location so we check that
            if (getNpcPath() == null) {
                lookAt(null, location, true);
                npcPojo.setLocation(new ZLocation(location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5, location.getYaw(), location.getPitch())));
            }
            // Set new npc location
            ClassTypes.SET_LOCATION_METHOD.invoke(nmsEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            // Update npc location
            updateLocation();
            // Update hologram location
            hologram.setLocation(location, npcPojo.getNpcType().getHoloHeight());
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
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
        // Set new profile properties (the skin values)
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
                ClassTypes.SET_DATA_WATCHER_METHOD.invoke(dataWatcherObject, ClassTypes.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(znpcSkin.getLayerIndex(), dataWatcherRegistryEnum), (byte) 127);
            } else ClassTypes.WATCH_DATA_WATCHER_METHOD.invoke(dataWatcherObject, 10, (byte) 127);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Changes the entity type of the npc.
     *
     * @param npcType The new entity type.
     */
    public void changeType(ZNPCType npcType) {
        try {
            Object nmsWorld = ClassTypes.GET_HANDLE_WORLD_METHOD.invoke(getLocation().getWorld());
            boolean isPlayer = npcType == ZNPCType.PLAYER;
            // Create the npc-player spawn packet
            if (isPlayer && packets.playerSpawnPacket == null) {
                packets.getPlayerPacket(nmsWorld);
            }
            nmsEntity = (isPlayer ? packets.playerSpawnPacket : (Utils.versionNewer(14) ? npcType.getConstructor().newInstance(npcType.getNmsEntityType(), nmsWorld) : npcType.getConstructor().newInstance(nmsWorld)));
            bukkitEntity = (ClassTypes.GET_BUKKIT_ENTITY_METHOD.invoke(nmsEntity));
            if (isPlayer) {
                tabConstructor = (ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.ADD_PLAYER_FIELD.get(null), Collections.singletonList(nmsEntity)));
                // Fix second layer skin for entity player
                setSecondLayerSkin();
                // Update npc glow-color for player
                if (getNpcPojo().isHasGlow() && Utils.versionNewer(9)) {
                    toggleGlow(npcPojo.getGlowName());
                }
            }
            npcPojo.setNpcType(npcType);
            // Teleport new npc type to the last or saved npc location
            setLocation(getLocation());
            // Update new npc type for viewers
            deleteViewers();
            // Update new entity id
            entityID = ((Integer) ClassTypes.GET_ENTITY_ID.invoke(nmsEntity));
            // Update npc packets
            packets.update();
            packets.destroyPacket = packets.getDestroyPacket(entityID);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Spawns the npc for the player.
     *
     * @param player The player to spawn the npc for.
     */
    public void spawn(Player player) {
        Preconditions.checkArgument(!viewers.contains(player), "The player %s is already a viewer", player.getName());
        try {
            // Determine if the npc type is player
            boolean npcIsPlayer = npcPojo.getNpcType() == ZNPCType.PLAYER;
            if (npcIsPlayer) {
                // Update scoreboard packets
                ReflectionUtils.sendPacket(player, packets.scoreboardDelete);
                ReflectionUtils.sendPacket(player, packets.scoreboardSpawn);
                if (npcPojo.isHasMirror()) {
                    // Set npc skin to the player skin
                    updateProfile(getGameProfileForPlayer(player).getProperties());
                }
                // Add npc to tabList
                ReflectionUtils.sendPacket(player, tabConstructor);
            }
            // Send npc spawn packets
            ReflectionUtils.sendPacket(player, npcIsPlayer ? ClassTypes.PACKET_PLAY_OUT_NAMED_ENTITY_CONSTRUCTOR.newInstance(nmsEntity) : ClassTypes.PACKET_PLAY_OUT_SPAWN_ENTITY_CONSTRUCTOR.newInstance(nmsEntity));
            if (npcPojo.isHasToggleHolo()) {
                hologram.spawn(player);
            }
            // Add player to viewers list
            viewers.add(player);
            // Update npc meta-data
            updateMetaData();
            // Update npc equipment
            sendEquipPackets(player);
            // Fix npc rotation
            lookAt(player, getLocation(), true);
            if (npcIsPlayer)
                ServersNPC.SCHEDULER.scheduleSyncDelayedTask(() ->
                                hideFromTab(player),
                        60
                );
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * @inheritDoc
     */
    public void sendEquipPackets(Player player) {
        packets.equipPackets.values().forEach(packet -> ReflectionUtils.sendPacket(player, packet));
    }

    /**
     * Hides the npc on tab-list for the given player.
     *
     * @param player The player to hide the npc for.
     */
    public void hideFromTab(Player player) {
        try {
            ReflectionUtils.sendPacket(player, ClassTypes.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(ClassTypes.REMOVE_PLAYER_FIELD.get(null), Collections.singletonList(nmsEntity)));
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Deletes the npc for the given player.
     *
     * @param player The player to delete the npc for.
     */
    public void delete(Player player, boolean removeViewer) {
        Preconditions.checkArgument(viewers.contains(player), "The player %s is not a viewer", player.getName());
        // Hide npc from tabList
        if (npcPojo.getNpcType() == ZNPCType.PLAYER) {
            hideFromTab(player);
        }
        ReflectionUtils.sendPacket(player, packets.destroyPacket);
        hologram.delete(player);
        if (removeViewer) {
            viewers.remove(player);
        }
    }

    /**
     * Makes the npc look at a location.
     *
     * @param location The location to look.
     */
    public void lookAt(Player player, Location location, boolean rotation) {
        if (System.currentTimeMillis() - lastMove < 1000 * 3) {
            return;
        }
        // Set location direction
        Location direction = (rotation ? location : npcPojo.getLocation().toBukkitLocation().clone().setDirection(location.clone().subtract(npcPojo.getLocation().toBukkitLocation().clone()).toVector()));
        try {
            Object lookPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.newInstance(entityID, (byte) (direction.getYaw() * 256.0F / 360.0F), (byte) (direction.getPitch() * 256.0F / 360.0F), true);
            Object headRotationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.newInstance(nmsEntity, (byte) (direction.getYaw() * 256.0F / 360.0F));
            // Send npc rotation packets to players
            if (player != null) {
                ReflectionUtils.sendPacket(player, lookPacket, headRotationPacket);
            } else {
                viewers.forEach(players -> ReflectionUtils.sendPacket(players, headRotationPacket));
            }
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
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
     * Updates the npc equipment for current viewers.
     */
    public void updateEquipment(ZNPCSlot slot, ItemStack stack) {
        try {
            npcPojo.getNpcEquip().put(slot, stack);
            if (Utils.BUKKIT_VERSION >= 16) {
                packets.getEquipPacket();
            } else {
                packets.getEquipPacket(slot, stack);
            }
            // Update new npc equipment for all viewers
            viewers.forEach(this::sendEquipPackets);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Deletes the NPC for current viewers.
     */
    public void deleteViewers() {
        if (viewers.isEmpty()) {
            return;
        }

        Iterator<Player> iterator = viewers.iterator();
        do {
            delete(iterator.next(), false);
            iterator.remove();
        } while (iterator.hasNext());
    }

    /**
     * Updates the npc game-profile.
     */
    public void updateProfile(PropertyMap propertyMap) {
        // Only human npc can have a profile
        if (npcPojo.getNpcType() != ZNPCType.PLAYER) {
            return;
        }

        try {
            Object gameProfileObj = ClassTypes.GET_PROFILE_METHOD.invoke(nmsEntity);
            ReflectionUtils.setValue(gameProfileObj, "name", gameProfile.getName());
            ReflectionUtils.setValue(gameProfileObj, "id", gameProfile.getId());
            ReflectionUtils.setValue(gameProfileObj, "properties", propertyMap);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * @inheritDoc
     */
    public void updateMetaData() {
        try {
            Object customizationPacket = ClassTypes.PACKET_PLAY_OUT_ENTITY_META_DATA_CONSTRUCTOR.newInstance(entityID, ClassTypes.GET_DATA_WATCHER_METHOD.invoke(nmsEntity), true);
            viewers.forEach(player -> ReflectionUtils.sendPacket(player, customizationPacket));
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
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
        return getNpcPath() != null ?
                getNpcPath().getLocation().toBukkitLocation() :
                npcPojo.getLocation().toBukkitLocation();
    }

    /**
     * @inheritDoc
     */
    public void setLastMove(long lastMove) {
        this.lastMove = lastMove;
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
        if (znpc == null) {
            return;
        }

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