package ak.znetwork.znpcservers.user;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.events.NPCInteractEvent;
import ak.znetwork.znpcservers.events.type.ClickType;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.ZNPCAction;
import ak.znetwork.znpcservers.types.ClassTypes;

import ak.znetwork.znpcservers.utility.Utils;
import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import lombok.Data;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Data
public class ZUser {
    /**
     * The name of the NPC interact channel.
     */
    private static final String CHANNEL_NAME = "npc_interact";

    /**
     * The default wait time between each npc interact.
     */
    private static int DEFAULT_DELAY = 1;

    /**
     * A map containing the saved users.
     */
    private static final Map<UUID, ZUser> USER_MAP = new HashMap<>();

    /**
     * A map for checking the last interact time for an NPC.
     */
    private final Map<Integer, Long> lastClicked;

    /**
     * Store event services to run when the user trigger the event.
     */
    private final List<EventService<?>> eventServices;

    /**
     * The player uuid.
     */
    private final UUID uuid;

    /**
     * The player channel.
     */
    private final Channel channel;

    /**
     * The player game-profile.
     */
    private final GameProfile gameProfile;

    /**
     * Determines if player is creating a npc path.
     */
    private boolean hasPath = false;

    /**
     * Used to compare the interaction time when a npc is clicked.
     */
    private long lastInteract = 0;

    /**
     * Creates a new user for the given uuid.
     *
     * @param uuid The player uuid.
     */
    public ZUser(UUID uuid) {
        this.uuid = uuid;
        lastClicked = new HashMap<>();
        eventServices = new ArrayList<>();
        // handle npc user channel
        try {
            final Object playerHandle = ClassTypes.GET_HANDLE_PLAYER_METHOD.invoke(toPlayer());
            gameProfile = (GameProfile) ClassTypes.GET_PROFILE_METHOD.invoke(playerHandle);
            channel = (Channel) ClassTypes.CHANNEL_FIELD.get(ClassTypes.NETWORK_MANAGER_FIELD.get(ClassTypes.PLAYER_CONNECTION_FIELD.get(playerHandle)));
            // check if channel is already created on the user network
            if (channel.pipeline().names().contains(CHANNEL_NAME)) {
                // channel found, remove it
                channel.pipeline().remove(CHANNEL_NAME);
            }
            // setup npc channel for user
            channel.pipeline().addAfter("decoder", CHANNEL_NAME, new ZNPCSocketDecoder());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("can't create player " + uuid.toString(), e.getCause());
        }
    }

    /**
     * Returns the user as a player.
     *
     * @return The user player.
     */
    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Returns the user instance for the given uuid.
     *
     * @param uuid The uuid.
     * @return The user instance.
     */
    public static ZUser find(UUID uuid) {
        return USER_MAP.computeIfAbsent(uuid, u -> new ZUser(uuid));
    }

    /**
     * Returns the user instance for the given player.
     *
     * @param player The player.
     * @return The user instance.
     */
    public static ZUser find(Player player) {
        return find(player.getUniqueId());
    }

    /**
     * Unregisters the user for the given player.
     *
     * @param player The player.
     */
    public static void unregister(Player player) {
        ZUser znpcUser = USER_MAP.get(player.getUniqueId());
        if (znpcUser == null) {
            throw new IllegalStateException("can't find user " + player.getUniqueId());
        }
        // delete all npc for the user player
        ZNPC.all().stream()
                .filter(npc -> npc.getViewers().contains(player))
                .forEach(npc -> npc.delete(player, true));
        USER_MAP.remove(player.getUniqueId());
    }

    /**
     * Listens when a player interact with an npc.
     *
     * A {@link MessageToMessageDecoder} which tracks when a user interacts with an npc.
     */
    class ZNPCSocketDecoder extends MessageToMessageDecoder<Object> {
        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, Object packet, List<Object> out) throws Exception {
            out.add(packet);
            if (packet.getClass() == ClassTypes.PACKET_PLAY_IN_USE_ENTITY_CLASS) {
                // check for interact wait time between npc
                long lastInteractNanos = System.nanoTime() - lastInteract;
                if (lastInteract != 0 && lastInteractNanos < Utils.SECOND_INTERVAL_NANOS * DEFAULT_DELAY) {
                    return;
                }
                // the clicked entity id
                int entityId = ClassTypes.PACKET_IN_USE_ENTITY_ID_FIELD.getInt(packet);
                // find npc
                ZNPC npc = ZNPC.all().stream().filter(znpc -> znpc.getEntityID() == entityId).findFirst().orElse(null);
                if (npc == null) {
                    return;
                }
                // determine if click is right or left
                ClickType clickName = ClickType.forName(npc.getPackets().getClickType(packet).toString());
                lastInteract = System.nanoTime();
                ServersNPC.SCHEDULER.scheduleSyncDelayedTask(() -> {
                    // call NPC interact event
                    Bukkit.getServer().getPluginManager().callEvent(new NPCInteractEvent(toPlayer(), clickName, npc));
                    final List<ZNPCAction> actions = npc.getNpcPojo().getClickActions();
                    // check if the clicked npc have actions
                    if (actions == null || actions.isEmpty()) {
                        return;
                    }
                    // run npc actions for player
                    for (ZNPCAction npcAction : actions) {
                        if (npcAction.getClickType() != ClickType.DEFAULT
                                && clickName != npcAction.getClickType()) {
                            continue;
                        }
                        // handle delay for action
                        if (npcAction.getDelay() > 0) {
                            int actionId = npc.getNpcPojo().getClickActions().indexOf(npcAction);
                            // check for action interact time
                            if (lastClicked.containsKey(actionId)) {
                                long lastClickNanos = System.nanoTime() - lastClicked.get(actionId);
                                if (lastClickNanos < npcAction.getFixedDelay()) {
                                    continue;
                                }
                            }
                            // update last interact time for action
                            lastClicked.put(actionId, System.nanoTime());
                        }
                        npcAction.run(ZUser.this, npcAction.getAction());
                    }
                }, DEFAULT_DELAY);
            }
        }
    }
}