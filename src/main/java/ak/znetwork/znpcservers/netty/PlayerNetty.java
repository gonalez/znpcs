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
import ak.znetwork.znpcservers.npc.NPC;
import ak.znetwork.znpcservers.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

public class PlayerNetty {

    protected Class<?> packetPlayInUseEntity;

    protected Object networkManager;
    protected Object channel;

    protected UUID uuid;

    protected Field idField;

    protected final ServersNPC serversNPC;

    public PlayerNetty(final ServersNPC serversNPC , final Player player) {
        this.serversNPC = serversNPC;

        this.uuid = player.getUniqueId();

        try {
            packetPlayInUseEntity = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayInUseEntity");

            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);

            networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
            channel = networkManager.getClass().getField("channel").get(networkManager);

            idField = packetPlayInUseEntity.getDeclaredField("a");
            idField.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void injectNetty(final Player player) {
        try {
            Channel channel = (Channel) this.channel;

            if (channel.pipeline().get("npc_interact") != null) {
                channel.pipeline().remove("npc_interact");
            }

            channel.pipeline().addAfter("decoder", "npc_interact", new MessageToMessageDecoder<Object>() {
                @Override
                protected void decode(ChannelHandlerContext chc, Object packet, List<Object> out) {
                    out.add(packet);

                    if (packet.getClass() == packetPlayInUseEntity) {
                        try {
                            Object className = ReflectionUtils.getValue(packet , "action");

                            if (className.toString().equalsIgnoreCase("INTERACT"))
                                return;

                            int entityId2 = (int) idField.get(packet);

                            final NPC npc = serversNPC.getNpcManager().getNpcs().stream().filter(npc1 -> npc1.getEntity_id() == entityId2).findFirst().orElse(null);

                            if (npc == null)
                                return;

                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (npc.getAction() != null) {
                                        final String action = npc.getAction().replace("_" , " ");

                                        switch (npc.getNpcAction()) {
                                            case CMD:
                                                player.performCommand(action);
                                                break;
                                            case SERVER:
                                                serversNPC.sendPlayerToServer(player , action);
                                                break;
                                            default:break;
                                        }
                                    }
                                }
                            }.runTaskLater(serversNPC , 2L);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ejectNetty(Player player) {
        try {
            Channel channel = (Channel) this.channel;
            if (channel != null) {
                if (channel.pipeline().get("npc_interact") != null) {
                    channel.pipeline().remove("npc_interact");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

