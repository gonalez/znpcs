package io.github.znetworkw.znpcservers.user.internal;

import io.github.znetworkw.znpcservers.ZNPCs;
import io.github.znetworkw.znpcservers.cache.CacheRegistry;
import io.github.znetworkw.znpcservers.npc.Npc;
import io.github.znetworkw.znpcservers.user.User;
import io.github.znetworkw.znpcservers.utility.PluginLocation;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * A default, reflective plugin user implementation.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ReflectivePluginUser implements User {
    private static final String CHANNEL_NAME = "znpc_interact";

    private final UUID uuid;
    private final Object playerConnection;

    /**
     * Constructs a new {@link User} for the given player.
     *
     * @param player the player.
     */
    public ReflectivePluginUser(Player player) throws Exception {
        this.uuid = player.getUniqueId();
        this.playerConnection = CacheRegistry.PLAYER_CONNECTION_FIELD.get(CacheRegistry.GET_HANDLE_PLAYER_METHOD.invoke(player));
        final Channel channel = (Channel) CacheRegistry.CHANNEL_FIELD.get(CacheRegistry.NETWORK_MANAGER_FIELD.get(playerConnection));
        if (channel.pipeline().names().contains(CHANNEL_NAME)) { // check if the channel is already registered on the user channel
            channel.pipeline().remove(CHANNEL_NAME);
        }
        channel.pipeline().addAfter("decoder", CHANNEL_NAME, new UserPacketDecoder(this));
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public Location getLocation() {
        return getPlayer().getLocation();
    }

    @Override
    public void sendPackets(Object... packets) throws Exception {
        for (Object packet : packets) {
            CacheRegistry.SEND_PACKET_METHOD.invoke(playerConnection, packet);
        }
    }

    /**
     * A message decoder for packets received from users.
     */
    private static final class UserPacketDecoder extends MessageToMessageDecoder<Object> {
        private static final long WAIT_TIME = TimeUnit.SECONDS.toNanos(1);

        private final User user;

        /**
         * Represents the last click timestamp with an npc.
         */
        private volatile long lastNpcInteract = 0L;

        public UserPacketDecoder(User user) {
            this.user = user;
        }

        @Override
        protected void decode(
            final ChannelHandlerContext channelHandlerContext,
            final Object object,
            final List<Object> decoded) throws Exception {
            decoded.add(object);
            if (object.getClass() == CacheRegistry.PACKET_PLAY_IN_USE_ENTITY_CLASS) {
                final Npc npc = findNpcForEntityID(CacheRegistry.PACKET_IN_USE_ENTITY_ID_FIELD.getInt(object));
                if (npc == null
                    || npc.getClickHandler() == null
                    || System.nanoTime() - lastNpcInteract < WAIT_TIME) {
                    return;
                }
                lastNpcInteract = System.nanoTime();
                npc.getClickHandler().onClick(npc, user);
            }
        }

        private Npc findNpcForEntityID(int entityId) {
            Npc find = null;
            for (Npc npc : ZNPCs.SETTINGS.getNpcStore().getNpcs()) {
                if (npc.getPluginEntity() != null && npc.getPluginEntity().getId() == entityId) {
                    find = npc;
                }
            }
            return find;
        }
    }
}
