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
import com.google.common.collect.HashBasedTable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter @Setter
public class ZNPCUser {

    /**
     * The name of the NPC Interact channel.
     */
    private static final String CHANNEL_NAME = "npc_interact";

    /**
     * An empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * The default wait time between each npc interact.
     */
    private static int DEFAULT_DELAY = 2;

    /**
     * The player network manager.
     */
    private final Object networkManager;

    /**
     * The player channel.
     */
    private final Channel channel;

    /**
     * The executor to delegate interaction work of a npc.
     */
    private final Executor executor;

    /**
     * A collection of cooldowns for each interacted NPC.
     */
    private final HashBasedTable<Integer, Long, Integer> actionDelay;

    /**
     * The player uuid.
     */
    private final UUID uuid;

    /**
     * Checks if player is creating a npc path.
     */
    private boolean hasPath = false;

    /**
     * Used to compare the interaction time when a npc is clicked.
     */
    private long lastInteract = 0;

    /**
     * The plugin instance.
     */
    private final ServersNPC serversNPC;

    /**
     * Creates a new USER Player.
     *
     * @param serversNPC The plugin instance.
     * @param player     The user player.
     * @throws Exception If the classes cannot be loaded.
     */
    public ZNPCUser(ServersNPC serversNPC,
                    Player player) throws Exception {
        this.serversNPC = serversNPC;
        this.uuid = player.getUniqueId();

        this.networkManager = ClassTypes.NETWORK_MANAGER_FIELD.get(ClassTypes.PLAYER_CONNECTION_FIELD.get(ClassTypes.GET_HANDLE_PLAYER_METHOD.invoke(player)));
        this.channel = (Channel) ClassTypes.CHANNEL_FIELD.get(networkManager);

        this.actionDelay = HashBasedTable.create();

        this.executor = r -> this.serversNPC.getServer().getScheduler().scheduleSyncDelayedTask(serversNPC, r, 2);

        this.injectNetty();
    }

    /**
     * Injects NPC channel to player channel.
     */
    public void injectNetty() {
        ejectNetty();

        getChannel().pipeline().addAfter("decoder", CHANNEL_NAME, new ZNPCSocketDecoder());
    }

    /**
     * Unregisters the NPC channel for player.
     */
    public void ejectNetty() {
        if (!getChannel().pipeline().names().contains(CHANNEL_NAME))
            return;

        getChannel().eventLoop().execute(() -> channel.pipeline().remove(CHANNEL_NAME));
    }

    /**
     * Gets player by user uuid.
     *
     * @return The player.
     */
    public Player toPlayer() {
        return Bukkit.getPlayer(getUuid());
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
                String useActionName = ReflectionUtils.getValue(packet, "action").toString();
                // Only allow interact by right-click.
                if (!useActionName.equalsIgnoreCase("INTERACT"))
                    return;

                // Check for interact wait time between npc
                if (getLastInteract() > 0 && System.currentTimeMillis() - getLastInteract() <= 1000L * DEFAULT_DELAY)
                    return;

                // The clicked entity id
                int entityId = (int) ClassTypes.PACKET_IN_USE_ENTITY_ID_FIELD.get(packet);

                // Try find npc
                ZNPC znpc = ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getEntityId() == entityId).findFirst().orElse(null);
                if (znpc == null)
                    return;

                setLastInteract(System.currentTimeMillis());

                executor.execute(() -> {
                    // Call NPC interact event
                    Bukkit.getServer().getPluginManager().callEvent(new NPCInteractEvent(toPlayer(), znpc));

                    if (znpc.getActions() == null || znpc.getActions().isEmpty())
                        return;

                    for (String string : znpc.getActions()) {
                        String[] actions = string.split(":");

                        // Get npc action type
                        NPCAction npcAction = NPCAction.fromString(actions[0]);
                        if (npcAction == null)
                            return;

                        String actionValue = actions[1];
                        // Run action for the provided actionValue
                        npcAction.run(ZNPCUser.this, toPlayer(), Utils.PLACEHOLDER_SUPPORT ?
                                PlaceholderUtils.getWithPlaceholders(toPlayer(), actionValue) :
                                actionValue
                        );

                        // Check for action cooldown
                        if (actions.length > 2) {
                            int actionId = znpc.getActions().indexOf(string);

                            // Set new action cooldown for user
                            getActionDelay().put(actionId, System.currentTimeMillis(), Integer.parseInt(actions[actions.length - 1]));
                        }
                    }
                });
            }
        }
    }
}


