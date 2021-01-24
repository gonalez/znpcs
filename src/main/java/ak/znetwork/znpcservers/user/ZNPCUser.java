/*
 *
 * ZNServersNPC
 * Copyright (C) 2019 Gaston Gonzalez (ZNetwork)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package ak.znetwork.znpcservers.user;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.cache.ClazzCache;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.npc.path.writer.ZNPCPathWriter;
import ak.znetwork.znpcservers.utils.PlaceholderUtils;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import ak.znetwork.znpcservers.utils.Utils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * Netty API
 *
 * @author ZNetwork
 * <p>
 * <p>
 * TODO
 * - CACHE MORE
 */
public class ZNPCUser {

    private final Object networkManager;

    private final Channel channel;

    private final UUID uuid;

    private final Field idField;

    private final ServersNPC serversNPC;

    private long last_interact = 0;

    private final HashMap<Integer, Map<Long, Integer>> actionCooldown;

    private final Executor executor;

    private ZNPCPathWriter znpcPathCreator;

    public ZNPCUser(final ServersNPC serversNPC, final Player player) throws Exception {
        this.serversNPC = serversNPC;

        this.uuid = player.getUniqueId();
        this.actionCooldown = new HashMap<>();

        Object craftPlayer = ClazzCache.GET_HANDLE_PLAYER_METHOD.method.invoke(player);
        Object playerConnection = ClazzCache.PLAYER_CONNECTION_FIELD.field.get(craftPlayer);

        networkManager = ClazzCache.NETWORK_MANAGER_FIELD.field.get(playerConnection);
        channel = (Channel) ClazzCache.CHANNEL_FIELD.field.get(networkManager);

        idField = ClazzCache.ID_FIELD.field;

        executor = r -> this.serversNPC.getServer().getScheduler().scheduleSyncDelayedTask(serversNPC, r, 2);
    }

    public UUID getUuid() {
        return uuid;
    }

    public void injectNetty(final Player player) {
        synchronized (networkManager) {
            if (channel == null) throw new IllegalStateException("Channel is NULL!");

            ejectNetty();
            channel.pipeline().addAfter("decoder", "npc_interact", new MessageToMessageDecoder<Object>() {
                @Override
                protected void decode(ChannelHandlerContext chc, Object packet, List<Object> out) throws Exception {
                    out.add(packet);

                    if (packet.getClass() == ClazzCache.PACKET_PLAY_IN_USE_ENTITY_CLASS.aClass) {
                        Object actionName = ReflectionUtils.getValue(packet, "action");

                        if (!actionName.toString().equalsIgnoreCase("INTERACT")) return;
                        if (last_interact > 0 && !(System.currentTimeMillis() - last_interact >= 1000 * 2)) return;

                        int entityId = (int) idField.get(packet);
                        serversNPC.getNpcManager().getNpcs().stream().filter(npc1 -> npc1.getEntity_id() == entityId).findFirst().ifPresent(npc -> {
                            if (npc.getActions() == null || npc.getActions().isEmpty()) return;

                            last_interact = System.currentTimeMillis();

                            executor.execute(() -> {
                                for (String string : npc.getActions()) {
                                    String[] actions = string.split(":");

                                    NPCAction npcAction = NPCAction.fromString(actions[0]);
                                    if (npcAction == null) return;

                                    if (actions.length > 1) {
                                        int id = npc.getActions().indexOf(string);

                                        // Check for cooldown
                                        if (actionCooldown.containsKey(id) && !(System.currentTimeMillis() - (long) actionCooldown.get(id).keySet().toArray()[0] >= 1000 * (int) actionCooldown.get(id).values().toArray()[0]))
                                            return;

                                        if (actions.length > 2)
                                            actionCooldown.put(1, Collections.singletonMap(System.currentTimeMillis(), 1));


                                        String action = (ServersNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(player, actions[1]) : actions[1]);
                                        switch (npcAction) {
                                            case CMD:
                                                player.performCommand(action);
                                                break;
                                            case CONSOLE:
                                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action);
                                                break;
                                            case SERVER:
                                                serversNPC.sendPlayerToServer(player, action);
                                                break;
                                            case MESSAGE:
                                                player.sendMessage(Utils.color(action));
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

    public void ejectNetty() {
        if (channel.pipeline().names().contains("npc_interact")) channel.pipeline().remove("npc_interact");
    }

    /*
    Other
     */

    public Player toPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public ZNPCPathWriter getZnpcPathCreator() {
        return znpcPathCreator;
    }

    public void setZnpcPathCreator(ZNPCPathWriter znpcPathCreator) {
        this.znpcPathCreator = znpcPathCreator;
    }
}


