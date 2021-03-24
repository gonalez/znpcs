package ak.znetwork.znpcservers.user;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.events.NPCInteractEvent;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.utility.PlaceholderUtils;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;

import com.mojang.authlib.GameProfile;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lombok.Data;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Data
public class ZNPCUser {

    /**
     * The name of the NPC Interact channel.
     */
    private static final String CHANNEL_NAME = "npc_interact";

    /**
     * The default wait time between each npc interact.
     */
    private static int DEFAULT_DELAY = 1;

    /**
     * A map containing the saved users.
     */
    private static final Map<UUID, ZNPCUser> USER_MAP = new HashMap<>();

    /**
     * The player channel.
     */
    private final Channel channel;

    /**
     * A map of cooldowns for each interacted NPC.
     */
    private final Map<Integer, Long> actionDelay;

    /**
     * The player uuid.
     */
    private final UUID uuid;

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
     * Creates a new USER Player.
     *
     * @param player The player.
     */
    public ZNPCUser(Player player) throws InvocationTargetException, IllegalAccessException {
        this.uuid = player.getUniqueId();

        final Object playerHandle = ClassTypes.GET_HANDLE_PLAYER_METHOD.invoke(player);
        final Object playerNetwork = ClassTypes.NETWORK_MANAGER_FIELD.get(ClassTypes.PLAYER_CONNECTION_FIELD.get(playerHandle));

        this.channel = (Channel) ClassTypes.CHANNEL_FIELD.get(playerNetwork);
        this.gameProfile = (GameProfile) ClassTypes.GET_PROFILE_METHOD.invoke(playerHandle);

        this.actionDelay = new HashMap<>();
        this.injectNetty();
    }

    /**
     * Injects NPC channel to player channel.
     */
    public void injectNetty() {
        ejectNetty();

        channel.pipeline().addAfter("decoder", CHANNEL_NAME, new ZNPCSocketDecoder());
    }

    /**
     * Unregisters the NPC channel for player.
     */
    public void ejectNetty() {
        if (!channel.pipeline().names().contains(CHANNEL_NAME))
            return;

        channel.eventLoop().execute(() -> channel.pipeline().remove(CHANNEL_NAME));
    }

    /**
     * Returns player by user uuid.
     *
     * @return The player.
     */
    public Player toPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    /**
     * Returns the user instance for the given player.
     *
     * @param player The player to add/find.
     * @return The user instance for the given player.
     */
    public static ZNPCUser registerOrGet(Player player) {
        ZNPCUser findUser = USER_MAP.get(player.getUniqueId());
        if (findUser != null)
            return findUser;

        try {
            return USER_MAP.put(player.getUniqueId(), new ZNPCUser(player));
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot register player " + player.getName());
        }
    }

    /**
     * Finds and unregister the user for the given uuid.
     *
     * @param uuid The player uuid.
     */
    public static void unregister(UUID uuid) {
        ZNPCUser znpcUser = USER_MAP.get(uuid);
        if (znpcUser == null)
            return;

        znpcUser.ejectNetty();
        USER_MAP.remove(uuid);
    }

    /**
     * Listens when a player interact with an npc.
     *
     * A {@link MessageToMessageDecoder} which tracks when a user interacts with an npc.
     */
    class ZNPCSocketDecoder extends MessageToMessageDecoder<Object> {

        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, Object packet, List<Object> out) throws Exception {
            if (channel == null)
                throw new IllegalStateException("Channel is NULL!");

            out.add(packet);

            if (packet.getClass() == ClassTypes.PACKET_PLAY_IN_USE_ENTITY_CLASS) {
                // Check for interact wait time between npc
                if (lastInteract > 0 && System.currentTimeMillis() - lastInteract <= 1000L * DEFAULT_DELAY)
                    return;

                // The clicked entity id
                int entityId = ClassTypes.PACKET_IN_USE_ENTITY_ID_FIELD.getInt(packet);

                // Try find npc
                ZNPC znpc = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getEntityId() == entityId).findFirst().orElse(null);
                if (znpc == null)
                    return;

                String clickName = ReflectionUtils.getValue(packet, "action").toString();
                lastInteract = System.currentTimeMillis();

                ServersNPC.SCHEDULER.scheduleSyncDelayedTask(() -> {
                    // Call NPC interact event
                    Bukkit.getServer().getPluginManager().callEvent(new NPCInteractEvent(toPlayer(), NPCInteractEvent.ClickType.forName(clickName), znpc));

                    if (znpc.getActions() == null || znpc.getActions().isEmpty())
                        return;

                    for (String string : znpc.getActions()) {
                        String[] actions = string.split(":");

                        // Check for action cooldown
                        if (actions.length > 2) {
                            int actionId = znpc.getActions().indexOf(string);

                            int actionCooldown = Integer.parseInt(actions[actions.length - 1]);
                            if (System.currentTimeMillis() - actionDelay.getOrDefault(actionId, 0L) < 1000L * actionCooldown)
                                return;

                            // Set new action cooldown for user
                            actionDelay.put(actionId, System.currentTimeMillis());
                        }

                        // Get npc action type
                        NPCAction npcAction = NPCAction.valueOf(actions[0]);

                        // Run action for the provided actionValue
                        String actionValue = actions[1];

                        npcAction.run(ZNPCUser.this, Utils.PLACEHOLDER_SUPPORT ?
                                PlaceholderUtils.getWithPlaceholders(toPlayer(), actionValue) :
                                actionValue
                        );
                    }
                }, DEFAULT_DELAY);
            }
        }
    }
}


