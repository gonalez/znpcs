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
package ak.znetwork.znpcservers.netty;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.cache.ClazzCache;
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.npc.enums.NPCAction;
import ak.znetwork.znpcservers.utils.PlaceholderUtils;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Netty API
 *
 * @author ZNetwork
 *
 *
 * TODO
 * - CACHE MORE
 */
public class PlayerNetty {

    protected final Object networkManager;
    protected Channel channel;

    protected UUID uuid;

    protected Field idField;

    protected final ServersNPC serversNPC;

    protected long last_interact = 0;

    protected HashMap<Integer, HashMap<Long, Integer>> actionCooldown;

    public PlayerNetty(final ServersNPC serversNPC , final Player player) throws Exception {
        this.serversNPC = serversNPC;

        this.uuid = player.getUniqueId();
        this.actionCooldown = new HashMap<>();

        Object craftPlayer = ClazzCache.GET_HANDLE_PLAYER_METHOD.method.invoke(player);
        Object playerConnection = ClazzCache.PLAYER_CONNECTION_FIELD.field.get(craftPlayer);

        networkManager = ClazzCache.NETWORK_MANAGER_FIELD.field.get(playerConnection);
        channel = (Channel) ClazzCache.CHANNEL_FIELD.field.get(networkManager);

        idField = ClazzCache.ID_FIELD.field;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void injectNetty(final Player player) {
        if (channel == null) throw new IllegalStateException("Channel is NULL!");

        ejectNetty();
        synchronized (networkManager) {
               channel.pipeline().addAfter("decoder", "npc_interact", new MessageToMessageDecoder<Object>() {
                   @Override
                   protected void decode(ChannelHandlerContext chc, Object packet, List<Object> out) {
                       out.add(packet);

                       if (packet.getClass() == ClazzCache.PACKET_PLAY_IN_USE_ENTITY_CLASS.aClass) {
                           try {
                               Object className = ReflectionUtils.getValue(packet, "action");

                               if (last_interact > 0 && !(System.currentTimeMillis() - last_interact >= 1000 * 2) || !className.toString().equalsIgnoreCase("INTERACT")) return;

                               int entityId2 = (int) idField.get(packet);

                               final NPC npc = serversNPC.getNpcManager().getNpcs().stream().filter(npc1 -> npc1.getEntity_id() == entityId2).findFirst().orElse(null);

                               if (npc == null || npc.getActions() == null || npc.getActions().isEmpty()) return;

                               last_interact = System.currentTimeMillis();

                               new BukkitRunnable() {
                                   @Override
                                   public void run() {
                                       for (String string : npc.getActions()) {
                                           final String[] actions = string.split(":");

                                           final NPCAction npcAction = NPCAction.fromString(actions[0]);

                                           if (npcAction == null) return;

                                           final String action = actions[1];
                                           if (actions.length > 2) {
                                               final int id = npc.getActions().indexOf(string);

                                               // Check for action cooldown
                                               if (actionCooldown.containsKey(id) && !(System.currentTimeMillis() - (long) actionCooldown.get(id).keySet().toArray()[0] >= 1000 * (int) actionCooldown.get(id).values().toArray()[0])) return;

                                               actionCooldown.put(id, new HashMap<Long, Integer>() {{
                                                   put(System.currentTimeMillis(), Integer.parseInt(actions[2]));
                                               }});
                                           }

                                           switch (npcAction) {
                                               case CMD:
                                                   player.performCommand((ServersNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(player, action) : action));
                                                   break;
                                               case CONSOLE:
                                                   Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (ServersNPC.isPlaceHolderSupport() ? PlaceholderUtils.getWithPlaceholders(player, action) : action));
                                                   break;
                                               case SERVER:
                                                   serversNPC.sendPlayerToServer(player, action);
                                                   break;
                                           }
                                       }
                                   }
                               }.runTaskLater(serversNPC, 2L);
                           } catch (Exception e) {
                               // Ignore...
                           }
                       }
                   }
               });
           }
    }

    public void ejectNetty() {
        channel.eventLoop().execute(() -> {
            if (channel.pipeline().names().contains("npc_interact")) channel.pipeline().remove("npc_interact");
        });
    }
}

