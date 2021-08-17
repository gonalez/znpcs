package ak.znetwork.znpcservers.user;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.NPCAction;
import ak.znetwork.znpcservers.npc.event.NPCInteractEvent;
import ak.znetwork.znpcservers.npc.event.ClickType;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.cache.CacheRegistry;

import ak.znetwork.znpcservers.utility.Utils;
import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Represents a user.
 */
public class ZUser {
    /** The name of the NPC interact channel. */
    private static final String CHANNEL_NAME = "npc_interact";

    /** The default wait time between each npc interact. */
    private static final int DEFAULT_DELAY = 1;

    /** A map containing the saved users. */
    private static final Map<UUID, ZUser> USER_MAP = new HashMap<>();

    /** A map for checking the last interact time for an NPC. */
    private final Map<Integer, Long> lastClicked;

    /**
     * A map of event services to run when the user trigger the specified service event.
     * @since 3.4
     */
    private final List<EventService<?>> eventServices;

    /** The user uuid. */
    private final UUID uuid;

    /** The user game profile. */
    private final GameProfile gameProfile;

    /** The user connection field. */
    private final Object playerConnection;

    /**
     * {@code true} if the user is creating a npc path.
     */
    private boolean hasPath = false;

    /** Used to compare the last interaction with an npc. */
    private long lastInteract = 0;

    /**
     * Creates a {@link ZUser} for the given uuid.
     *
     * @param uuid The uuid.
     */
    public ZUser(UUID uuid) {
        this.uuid = uuid;
        lastClicked = new HashMap<>();
        eventServices = new ArrayList<>();
        try { // handle npc user channel
            final Object playerHandle = CacheRegistry.GET_HANDLE_PLAYER_METHOD.invoke(toPlayer());
            gameProfile = (GameProfile) CacheRegistry.GET_PROFILE_METHOD.invoke(playerHandle);
            // the user channel
            Channel channel = (Channel) CacheRegistry.CHANNEL_FIELD.get(CacheRegistry.NETWORK_MANAGER_FIELD.get(playerConnection = CacheRegistry.PLAYER_CONNECTION_FIELD.get(playerHandle)));
            if (channel.pipeline().names().contains(CHANNEL_NAME)) { // check if the channel is already created on the user network
                channel.pipeline().remove(CHANNEL_NAME);
            }
            // setup npc channel for user
            channel.pipeline().addAfter("decoder", CHANNEL_NAME, new ZNPCSocketDecoder());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("can't create player " + uuid.toString(), e.getCause());
        }
    }

    /**
     * Returns the user uuid.
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Returns the user game profile.
     */
    public GameProfile getGameProfile() {
        return gameProfile;
    }

    /**
     * Returns the user connection field.
     */
    public Object getPlayerConnection() {
        return playerConnection;
    }

    /**
     * Returns {@code true} if the user is creating a path.
     */
    public boolean isHasPath() {
        return hasPath;
    }

    /**
     * A list of all {@link EventService}s to run when the user trigger the specified event.
     */
    public List<EventService<?>> getEventServices() {
        return eventServices;
    }

    /**
     * Sets the {@link #isHasPath()} of this user.
     *
     * @param hasPath The new value.
     */
    public void setHasPath(boolean hasPath) {
        this.hasPath = hasPath;
    }

    /**
     * Returns the user as a {@link Player}.
     */
    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Tries to find a user for the given {@code uuid}.
     * If no user found, register a new user with the given {@code uuid}.
     *
     * @param uuid user {@link #getUUID()}.
     * @return If the user was found, return the user.
     * Otherwise return a new instance for the given uuid.
     */
    public static ZUser find(UUID uuid) {
        return USER_MAP.computeIfAbsent(uuid, ZUser::new);
    }

    /**
     * Tries to find a user for the player {@link Player#getUniqueId()}.
     * @see #find(UUID)
     */
    public static ZUser find(Player player) {
        return find(player.getUniqueId());
    }

    /**
     * Deletes the user for the given player {@link Player#getUniqueId()}.
     *
     * @param player
     *     The player in which their uuid will be used to finding
     *     and deleting the user.
     * @throws IllegalStateException If the user can't be found.
     */
    public static void unregister(Player player) {
        ZUser zUser = USER_MAP.get(player.getUniqueId());
        if (zUser == null) {
            throw new IllegalStateException("can't find user " + player.getUniqueId());
        }
        USER_MAP.remove(player.getUniqueId());
        // delete all npc for the player
        NPC.all().stream()
                .filter(npc -> npc.getNpcViewers().contains(zUser))
                .forEach(npc -> npc.delete(zUser, true));
    }

    /**
     * Listens when a player interact with an npc.
     * <p />
     * A {@link MessageToMessageDecoder} which tracks when a user interacts with an npc.
     */
    class ZNPCSocketDecoder extends MessageToMessageDecoder<Object> {
        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, Object packet, List<Object> out) throws Exception {
            out.add(packet);
            if (packet.getClass() == CacheRegistry.PACKET_PLAY_IN_USE_ENTITY_CLASS) {
                // check for interact wait time between npc
                long lastInteractNanos = System.nanoTime() - lastInteract;
                if (lastInteract != 0 && lastInteractNanos < Utils.SECOND_INTERVAL_NANOS * DEFAULT_DELAY) {
                    return;
                }
                int entityId = CacheRegistry.PACKET_IN_USE_ENTITY_ID_FIELD.getInt(packet);
                // try find npc
                NPC npc = NPC.all().stream()
                        .filter(npc1 -> npc1.getEntityID() == entityId)
                        .findFirst()
                        .orElse(null);
                if (npc == null) {
                    return;
                }
                ClickType clickName = ClickType.forName(npc.getPackets().getClickType(packet).toString()); // determine click type /right/left
                lastInteract = System.nanoTime();
                ServersNPC.SCHEDULER.scheduleSyncDelayedTask(() -> {
                    Bukkit.getServer().getPluginManager().callEvent(new NPCInteractEvent(toPlayer(), clickName, npc));
                    final List<NPCAction> actions = npc.getNpcPojo().getClickActions();
                    if (actions == null || actions.isEmpty()) { // check if the npc have actions
                        return;
                    }
                    for (NPCAction npcAction : actions) {
                        if (npcAction.getClickType() != ClickType.DEFAULT
                                && clickName != npcAction.getClickType()) {
                            continue;
                        }
                        if (npcAction.getDelay() > 0) { // handle delay for action
                            int actionId = npc.getNpcPojo().getClickActions().indexOf(npcAction);
                            if (lastClicked.containsKey(actionId)) { // check for action interact time
                                long lastClickNanos = System.nanoTime() - lastClicked.get(actionId);
                                if (lastClickNanos < npcAction.getFixedDelay()) {
                                    continue;
                                }
                            }
                            lastClicked.put(actionId, System.nanoTime());
                        }
                        npcAction.run(ZUser.this, npcAction.getAction());
                    }
                }, DEFAULT_DELAY);
            }
        }
    }
}