package ak.znetwork.znpcservers.user;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.events.NPCInteractEvent;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.types.ClassTypes;
import ak.znetwork.znpcservers.utility.PlaceholderUtils;
import ak.znetwork.znpcservers.utility.ReflectionUtils;
import ak.znetwork.znpcservers.utility.Utils;
import com.google.common.collect.HashBasedTable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
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
    private long last_interact = 0;

    /**
     * The plugin instance.
     */
    private final ServersNPC serversNPC;

    /**
     * Creates a new USER Player.
     *
     * @param serversNPC The plugin instance.
     * @param player The user player.
     * @throws Exception If the classes cannot be loaded.
     */
    public ZNPCUser(ServersNPC serversNPC,
                    Player player) throws Exception {
        this.serversNPC = serversNPC;
        this.uuid = player.getUniqueId();

        this.actionDelay = HashBasedTable.create();
        this.networkManager = ClassTypes.NETWORK_MANAGER_FIELD.get(ClassTypes.PLAYER_CONNECTION_FIELD.get(ClassTypes.GET_HANDLE_PLAYER_METHOD.invoke(player)));this.channel = (Channel) ClassTypes.CHANNEL_FIELD.get(networkManager);

        this.executor = r -> this.serversNPC.getServer().getScheduler().scheduleSyncDelayedTask(serversNPC, r, 2);

        this.injectNetty();
    }

    /**
     * Inject NPC channel to player channel.
     */
    public void injectNetty() {
        ejectNetty();

        synchronized (networkManager) {
            if (channel == null) throw new IllegalStateException("Channel is NULL!");

            channel.pipeline().addAfter("decoder", CHANNEL_NAME, new MessageToMessageDecoder<Object>() {
                @Override
                protected void decode(ChannelHandlerContext chc, Object packet, List<Object> out) throws Exception {
                    out.add(packet);

                    if (packet.getClass() == ClassTypes.PACKET_PLAY_IN_USE_ENTITY_CLASS) {
                        Object actionName = ReflectionUtils.getValue(packet, "action");

                        if (!actionName.toString().equalsIgnoreCase("INTERACT")) return;
                        if (last_interact > 0 && !(System.currentTimeMillis() - last_interact >= 1000L * DEFAULT_DELAY)) return;

                        int entityId = (int) ClassTypes.PACKET_IN_USE_ENTITY_ID_FIELD.get(packet);
                        serversNPC.getNpcManager().getNpcList().stream().filter(npc1 -> npc1.getEntity_id() == entityId).findFirst().ifPresent(npc -> {
                            last_interact = System.currentTimeMillis();

                            executor.execute(() -> {
                                // Call NPC interact event
                                Bukkit.getServer().getPluginManager().callEvent(new NPCInteractEvent(toPlayer(), npc));

                                if (npc.getActions() == null || npc.getActions().isEmpty()) return;

                                for (String string : npc.getActions()) {
                                    String[] actions = string.split(":");

                                    NPCAction npcAction = NPCAction.fromString(actions[0]);
                                    if (npcAction == null) return;

                                    if (actions.length > 1) {
                                        int id = npc.getActions().indexOf(string);

                                        // Check for cooldown
                                        if (actionDelay.containsRow(id)) {
                                            Map.Entry<Long, Integer> delay = actionDelay.row(id).entrySet().iterator().next();

                                            if (System.currentTimeMillis() - delay.getKey() <= 1000L * delay.getValue())
                                                return;
                                        }

                                        if (actions.length > 2)
                                            actionDelay.put(id, System.currentTimeMillis(), Integer.parseInt(actions[actions.length - 1]));

                                        String action = (ServersNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(toPlayer(), actions[1]) : actions[1]);
                                        switch (npcAction) {
                                            case CMD:
                                                toPlayer().performCommand(action);
                                                break;
                                            case CONSOLE:
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action);
                                                break;
                                            case SERVER:
                                                serversNPC.sendPlayerToServer(toPlayer(), action);
                                                break;
                                            case MESSAGE:
                                                toPlayer().sendMessage(Utils.color(action));
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            });
                        });
                    }
                }
            });
        }
    }

    /**
     * Unregister NPC channel for player.
     */
    public void ejectNetty() {
        if (!channel.pipeline().names().contains(CHANNEL_NAME)) return;

        channel.eventLoop().execute(() -> channel.pipeline().remove(CHANNEL_NAME));
    }

    /**
     * Gets player by user uuid.
     *
     * @return The player.
     */
    public Player toPlayer() {
        return Bukkit.getPlayer(getUuid());
    }
}


