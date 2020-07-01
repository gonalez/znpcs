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

import ak.znetwork.znpcservers.utils.ReflectionUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class PlayerNetty {

    protected Class<?> packetPlayInUseEntity;

    protected Object networkManager;
    protected Object channel;

    public PlayerNetty(final Player player) {
        try {
            packetPlayInUseEntity = Class.forName("net.minecraft.server." + ReflectionUtils.getBukkitPackage() + ".PacketPlayInUseEntity");

            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);

            networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
            channel = networkManager.getClass().getField("channel").get(networkManager);
        } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void injectNetty(final Player player) {
        try {
            Channel channel = (Channel) this.channel;
            if (channel != null) {
                channel.pipeline().addAfter("decoder", "npc_interact", new MessageToMessageDecoder<Object>() {
                    @Override
                    protected void decode(ChannelHandlerContext chc, Object packet, List<Object> out) {
                        if (packet.getClass() == packetPlayInUseEntity) {
                            player.sendMessage("test");
                        }
                    }
                });
            }
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

