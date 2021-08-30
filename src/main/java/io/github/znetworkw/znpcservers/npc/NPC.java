package io.github.znetworkw.znpcservers.npc;

import com.google.common.collect.ImmutableList;
import io.github.znetworkw.znpcservers.ServersNPC;
import io.github.znetworkw.znpcservers.UnexpectedCallException;
import io.github.znetworkw.znpcservers.npc.hologram.Hologram;
import io.github.znetworkw.znpcservers.npc.conversation.ConversationModel;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.npc.packet.PacketCache;
import io.github.znetworkw.znpcservers.user.ZUser;
import io.github.znetworkw.znpcservers.utility.Utils;
import io.github.znetworkw.znpcservers.utility.location.ZLocation;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Represents a npc.
 */
public class NPC {
    /**
     * A map containing the stored npcs.
     */
    private static final ConcurrentMap<Integer, NPC> NPC_MAP = new ConcurrentHashMap<>();
    /** The {@link GameProfile} texture key name for the npc. */
    private static final String PROFILE_TEXTURES = "textures";
    /** The tab list prefix for npc. */
    private static final String START_PREFIX = ChatColor.DARK_GRAY + "[ZNPC] ";

    /** A set of players who can see the npc. */
    private final Set<ZUser> viewers = new HashSet<>();
    /** The npc packets. */
    private final PacketCache packets = new PacketCache();
    /** The npc model class. */
    private final NPCModel npcPojo;
    /** The npc hologram. */
    private final Hologram hologram;
    /** The npc tab name. */
    private final String npcName;
    /** The npc skin. */
    private final NPCSkin npcSkin;

    /** Last npc move. */
    private long lastMove = -1;
    /** The bukkit entity id. */
    private int entityID;
    /** The reflection variables. */
    private Object glowColor, tabConstructor, nmsEntity, bukkitEntity;
    /** The npc entity uuid. */
    private UUID uuid;
    /** The npc game profile. */
    private GameProfile gameProfile;
    /** The npc path. */
    private NPCPath.PathInitializer npcPath;

    /**
     * Creates a new {@link NPC}.
     *
     * @param npcModel The npc data model.
     */
    public NPC(NPCModel npcModel, boolean load) {
        this.npcPojo = npcModel;
        this.hologram = new Hologram(this);
        npcName = NamingType.DEFAULT.resolve(this);
        npcSkin = NPCSkin.forValues(npcModel.getSkin(), npcModel.getSignature());
        if (load) {
            onLoad();
        }
    }

    /**
     * Creates a new {@link NPC}.
     *
     * @param npcModel The npc data model.
     */
    public NPC(NPCModel npcModel) {
        this(npcModel, false);
    }

    /**
     * Initialization of all the necessary functions for the npc,
     * called when a npc is created for the first time.
     *
     * @throws IllegalStateException If the npc is already loaded.
     */
    public void onLoad() {
        if (NPC_MAP.containsKey(getNpcPojo().getId())) {
            throw new IllegalStateException("npc with id " + getNpcPojo().getId() + " already exists.");
        }

        gameProfile = new GameProfile(UUID.randomUUID(), START_PREFIX + npcName);
        gameProfile.getProperties().put(PROFILE_TEXTURES, new Property(PROFILE_TEXTURES, npcPojo.getSkin(), npcPojo.getSignature()));

        changeType(npcPojo.getNpcType());
        updateProfile(gameProfile.getProperties());

        setLocation(getNpcPojo().getLocation().bukkitLocation(), false);

        hologram.createHologram();

        if (npcPojo.getPathName() != null) {
            setPath(NPCPath.AbstractTypeWriter.find(npcPojo.getPathName()));
        }

        npcPojo.getCustomizationMap().forEach((key, value) -> npcPojo.getNpcType().updateCustomization(this, key, value));
        // register NPC in the map
        NPC_MAP.put(getNpcPojo().getId(), this);
    }

    /**
     * Returns the model instance of this npc.
     */
    public NPCModel getNpcPojo() {
        return npcPojo;
    }

    /**
     * Returns the npc uuid
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns the npc entity id.
     */
    public int getEntityID() {
        return entityID;
    }

    /**
     * Returns the npc bukkit entity.
     */
    public Object getBukkitEntity() {
        return bukkitEntity;
    }

    /**
     * Returns the npc nms entity.
     */
    public Object getNmsEntity() {
        return nmsEntity;
    }

    /**
     * Returns the npc glow color.
     */
    public Object getGlowColor() {
        return glowColor;
    }

    /**
     * Returns the npc {@link GameProfile}.
     */
    public GameProfile getGameProfile() {
        return gameProfile;
    }

    /**
     * Returns the npc path.
     */
    public NPCPath.PathInitializer getNpcPath() {
        return npcPath;
    }

    /**
     * Returns the npc hologram.
     */
    public Hologram getHologram() {
        return hologram;
    }

    /**
     * Returns a list of players who can see the npc.
     */
    public Set<ZUser> getViewers() {
        return viewers;
    }

    /**
     * Returns the npc packets instance.
     */
    public PacketCache getPackets() {
        return packets;
    }

    /**
     * Sets the {@link #getGlowColor()} of this npc.
     *
     * @param glowColor The npc glow color.
     */
    public void setGlowColor(Object glowColor) {
        this.glowColor = glowColor;
    }

    /**
     * Sets a new location for the npc.
     *
     * @param location The new location.
     */
    public void setLocation(Location location, boolean updateTime) {
        try {
            // if the npc has a path it will not look at the players
            // or location so we check that
            if (npcPath == null) {
                lookAt(null, location, true);
                if (updateTime) {
                    lastMove = System.nanoTime();
                }
                npcPojo.setLocation(new ZLocation(location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5, location.getYaw(), location.getPitch())));
            }
            CacheRegistry.SET_LOCATION_METHOD.invoke(nmsEntity, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
            final Object npcTeleportPacket = CacheRegistry.PACKET_PLAY_OUT_ENTITY_TELEPORT_CONSTRUCTOR.newInstance(nmsEntity);
            // update new location
            viewers.forEach(player -> Utils.sendPackets(player, npcTeleportPacket));
            // update the hologram location
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
    public void changeSkin(NPCSkin skinFetch) {
        npcPojo.setSkin(skinFetch.getTexture());
        npcPojo.setSignature(skinFetch.getSignature());
        // set new profile properties (skin values)
        gameProfile.getProperties().clear();
        gameProfile.getProperties().put(PROFILE_TEXTURES, new Property(PROFILE_TEXTURES, npcPojo.getSkin(), npcPojo.getSignature()));
        updateProfile(gameProfile.getProperties());
        // spawn npc again for viewers
        deleteViewers();
    }

    /**
     * Enables second layer of skin for the npc.
     */
    public void setSecondLayerSkin() {
        try {
            Object dataWatcherObject = CacheRegistry.GET_DATA_WATCHER_METHOD.invoke(nmsEntity);
            if (Utils.versionNewer(9)) {
                CacheRegistry.SET_DATA_WATCHER_METHOD.invoke(dataWatcherObject, CacheRegistry.DATA_WATCHER_OBJECT_CONSTRUCTOR.newInstance(npcSkin.getLayerIndex(), CacheRegistry.DATA_WATCHER_REGISTER_FIELD), (byte) 127);
            } else CacheRegistry.WATCH_DATA_WATCHER_METHOD.invoke(dataWatcherObject, 10, (byte) 127);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Changes the entity type of the npc.
     *
     * @param npcType The new entity type.
     */
    public void changeType(NPCType npcType) {
        try {
            final Object nmsWorld = CacheRegistry.GET_HANDLE_WORLD_METHOD.invoke(getLocation().getWorld());
            final boolean isPlayer = npcType == NPCType.PLAYER;
            nmsEntity = (isPlayer ? packets.getProxyInstance().getPlayerPacket(nmsWorld, gameProfile) : (Utils.versionNewer(14) ? npcType.getConstructor().newInstance(npcType.getNmsEntityType(), nmsWorld) : npcType.getConstructor().newInstance(nmsWorld)));
            bukkitEntity = (CacheRegistry.GET_BUKKIT_ENTITY_METHOD.invoke(nmsEntity));
            uuid = (UUID) CacheRegistry.GET_UNIQUE_ID_METHOD.invoke(nmsEntity);
            if (isPlayer) {
                tabConstructor = (CacheRegistry.PACKET_PLAY_OUT_PLAYER_INFO_CONSTRUCTOR.newInstance(CacheRegistry.ADD_PLAYER_FIELD, Collections.singletonList(nmsEntity)));
                setSecondLayerSkin();
            }
            npcPojo.setNpcType(npcType);
            // teleport new npc type to the last or saved npc location
            setLocation(getLocation(), false);
            packets.flushCache("spawnPacket", "removeTab"); // flush caches
            // update new npc type for viewers
            deleteViewers();
            entityID = ((Integer) CacheRegistry.GET_ENTITY_ID.invoke(nmsEntity));
            // run active functions
            FunctionFactory.findFunctionsForNpc(this).forEach(function -> function.resolve(this));
            getPackets().getProxyInstance().update(packets);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Spawns the npc for the given player.
     *
     * @param user The player to spawn the npc for.
     * @throws IllegalStateException If the given user is already a viewer.
     */
    public void spawn(ZUser user) {
        if (viewers.contains(user)) {
            throw new IllegalStateException(user.getUUID().toString() + " is already a viewer.");
        }
        try {
            final boolean npcIsPlayer = npcPojo.getNpcType() == NPCType.PLAYER;
            // check for scoreboard packets
            if (FunctionFactory.isTrue(this, "glow")
                || npcIsPlayer) {
                final ImmutableList<Object> scoreboardPackets = packets.getProxyInstance().updateScoreboard(this);
                scoreboardPackets.forEach(p -> Utils.sendPackets(user, p));
            }
            if (npcIsPlayer) {
                if (FunctionFactory.isTrue(this, "mirror")) {
                    // set npc skin to the player skin
                    updateProfile(user.getGameProfile().getProperties());
                }
                // add npc to tabList
                Utils.sendPackets(user, tabConstructor);
            }
            // send npc spawn packets
            Utils.sendPackets(user, packets.getProxyInstance().getSpawnPacket(nmsEntity, npcIsPlayer));
            if (FunctionFactory.isTrue(this, "holo")) {
                hologram.spawn(user);
            }
            viewers.add(user);
            updateMetadata(Collections.singleton(user));
            sendEquipPackets(user);
            // fix npc rotation
            lookAt(user, getLocation(), true);
            if (npcIsPlayer) {
                Object removeTabPacket = packets.getProxyInstance().getTabRemovePacket(nmsEntity);
                ServersNPC.SCHEDULER.scheduleSyncDelayedTask(() -> Utils.sendPackets(user, removeTabPacket), 60);
            }
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Deletes the npc for the given player.
     *
     * @param user The player to delete the npc for.
     * @throws IllegalStateException If the given user is not a viewer.
     */
    public void delete(ZUser user) {
        if (!viewers.contains(user)) {
            throw new IllegalStateException(user.getUUID().toString() + " is not a viewer.");
        }
        try {
            if (npcPojo.getNpcType() == NPCType.PLAYER) {
                packets.getProxyInstance().getTabRemovePacket(nmsEntity);
            }
            hologram.delete(user);
            Utils.sendPackets(user, packets.getProxyInstance().getDestroyPacket(entityID));
            viewers.remove(user);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Makes the npc look at a location.
     *
     * @param location The location to look.
     */
    public void lookAt(ZUser player,
                       Location location,
                       boolean rotation) {
        // check for last npc move
        long lastMoveNanos = System.nanoTime() - lastMove;
        if (lastMove > 1 &&
            lastMoveNanos < Utils.SECOND_INTERVAL_NANOS) {
            return;
        }
        // set the location direction
        Location direction = (rotation ? location : npcPojo.getLocation().bukkitLocation().clone().setDirection(location.clone().subtract(npcPojo.getLocation().bukkitLocation().clone()).toVector()));
        try {
            Object lookPacket = CacheRegistry.PACKET_PLAY_OUT_ENTITY_LOOK_CONSTRUCTOR.newInstance(entityID, (byte) (direction.getYaw() * 256.0F / 360.0F), (byte) (direction.getPitch() * 256.0F / 360.0F), true);
            Object headRotationPacket = CacheRegistry.PACKET_PLAY_OUT_ENTITY_HEAD_ROTATION_CONSTRUCTOR.newInstance(nmsEntity, (byte) (direction.getYaw() * 256.0F / 360.0F));
            if (player != null) {
                Utils.sendPackets(player, lookPacket, headRotationPacket);
            } else {
                viewers.forEach(players -> Utils.sendPackets(players, headRotationPacket));
            }
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Deletes the NPC for current viewers.
     */
    public void deleteViewers() {
        if (viewers.isEmpty()) {
            // npc have no viewers
            return;
        }
        for (ZUser user : getViewers()) {
            delete(user);
        }
        viewers.clear();
    }

    /**
     * Updates the npc meta data.
     */
    protected void updateMetadata(Iterable<ZUser> users) {
        try {
            Object metaData = packets.getProxyInstance().getMetadataPacket(entityID, nmsEntity);
            for (ZUser user : users) {
                Utils.sendPackets(user, metaData);
            }
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * Updates the npc {@link GameProfile}.
     */
    public void updateProfile(PropertyMap propertyMap) {
        if (npcPojo.getNpcType() != NPCType.PLAYER) { // non-players npc cannot have a profile
            return;
        }
        try {
            Object gameProfileObj = CacheRegistry.GET_PROFILE_METHOD.invoke(nmsEntity);
            Utils.setValue(gameProfileObj, "name", gameProfile.getName());
            Utils.setValue(gameProfileObj, "id", gameProfile.getId());
            Utils.setValue(gameProfileObj, "properties", propertyMap);
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException);
        }
    }

    /**
     * @inheritDoc
     */
    public void sendEquipPackets(ZUser zUser) {
        if (npcPojo.getNpcEquip().isEmpty()) {
            return;
        }
        try {
            ImmutableList<Object> equipPackets = packets.getProxyInstance().getEquipPackets(this);
            equipPackets.forEach(o -> Utils.sendPackets(zUser, o));
        } catch (ReflectiveOperationException operationException) {
            throw new UnexpectedCallException(operationException.getCause());
        }
    }

    /**
     * Sets a new path for the npc.
     *
     * @param typeWriter The new path.
     */
    public void setPath(NPCPath.AbstractTypeWriter typeWriter) {
        if (typeWriter == null) {
            npcPath = null;
            npcPojo.setPathName("none");
        } else {
            npcPath = typeWriter.getPath(this);
            npcPojo.setPathName(typeWriter.getName());
        }
    }

    /**
     * Starts the npc conversation for the given player.
     *
     * @param player The player to start the conversation with.
     * @throws IllegalStateException If cannot find conversation.
     */
    public void tryStartConversation(Player player) {
        ConversationModel conversation = npcPojo.getConversation();
        if (conversation == null) {
            throw new IllegalStateException("can't find conversation");
        }
        conversation.startConversation(this, player);
    }

    /**
     * Returns the current location of the npc.
     */
    public Location getLocation() {
        return npcPath != null ?
            npcPath.getLocation().bukkitLocation() :
            npcPojo.getLocation().bukkitLocation();
    }

    /**
     * Tries to locate a npc for the given {@code id}.
     *
     * @param id The id.
     * @return The found npc.
     */
    public static NPC find(int id) {
        return NPC_MAP.get(id);
    }

    /**
     * Unregisters a npc with the given {@code id}.
     *
     * @param id The id.
     * @throws IllegalStateException If cannot find npc.
     */
    public static void unregister(int id) {
        NPC npc = find(id);
        if (npc == null) {
            throw new IllegalStateException("can't find npc with id " + id);
        }
        NPC_MAP.remove(id);
        npc.deleteViewers();
    }

    /**
     * Returns the NPCs in the map.
     */
    public static Collection<NPC> all() {
        return NPC_MAP.values();
    }
}