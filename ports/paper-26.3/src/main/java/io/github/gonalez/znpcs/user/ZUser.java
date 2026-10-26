/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.user;

import com.mojang.authlib.GameProfile;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.npc.*;
import io.github.gonalez.znpcs.npc.event.*;
import io.netty.channel.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

/** Decode the distinct modern attack and interaction packets on the connection event loop. */
public class ZUser {
  private static final String CHANNEL_NAME = "znpcs_26_3_interact";
  private static final Map<UUID, ZUser> USER_MAP = new ConcurrentHashMap<>();
  private final Map<String, Long> lastClicked = new HashMap<>();
  private final List<EventService<?>> eventServices = new ArrayList<>();
  private final UUID uuid;
  private final GameProfile gameProfile;
  private final ServerGamePacketListenerImpl playerConnection;
  private final Channel channel;
  private boolean hasPath;
  private long lastInteract;

  public ZUser(UUID uuid) {
    this.uuid = uuid;
    var player = ((CraftPlayer) toPlayer()).getHandle();
    gameProfile = player.getGameProfile();
    playerConnection = player.connection;
    channel = playerConnection.connection.channel;
    channel
        .eventLoop()
        .execute(
            () -> {
              if (!channel.isActive()) return;
              if (channel.pipeline().get(CHANNEL_NAME) != null)
                channel.pipeline().remove(CHANNEL_NAME);
              channel
                  .pipeline()
                  .addBefore(
                      "packet_handler",
                      CHANNEL_NAME,
                      new ChannelInboundHandlerAdapter() {
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object packet)
                            throws Exception {
                          int id;
                          ClickType click;
                          if (packet instanceof ServerboundAttackPacket attack) {
                            id = attack.entityId();
                            click = ClickType.forName("ATTACK");
                          } else if (packet instanceof ServerboundInteractPacket use) {
                            id = use.entityId();
                            click = ClickType.forName("INTERACT");
                            if (use.hand() != InteractionHand.MAIN_HAND) {
                              super.channelRead(ctx, packet);
                              return;
                            }
                          } else {
                            super.channelRead(ctx, packet);
                            return;
                          }
                          NPC npc =
                              NPC.all().stream()
                                  .filter(n -> n.getEntityID() == id)
                                  .findFirst()
                                  .orElse(null);
                          if (npc == null) {
                            super.channelRead(ctx, packet);
                            return;
                          }
                          Bukkit.getScheduler()
                              .runTask(
                                  ServersNPC.getPlugin(ServersNPC.class),
                                  () -> interact(npc, click));
                        }
                      });
            });
  }

  private void interact(NPC npc, ClickType click) {
    Player p = toPlayer();
    if (p == null
        || !p.isOnline()
        || !npc.getViewers().contains(this)
        || p.getWorld() != npc.getLocation().getWorld()
        || p.getLocation().distanceSquared(npc.getLocation()) > 36) return;
    long now = System.nanoTime();
    if (lastInteract != 0 && now - lastInteract < 1_000_000_000L) return;
    lastInteract = now;
    Bukkit.getPluginManager().callEvent(new NPCInteractEvent(p, click, npc));
    List<NPCAction> actions = npc.getNpcPojo().getClickActions();
    if (actions == null) return;
    for (int i = 0; i < actions.size(); i++) {
      NPCAction action = actions.get(i);
      if (action.getClickType() != ClickType.DEFAULT && action.getClickType() != click) continue;
      String key = npc.getNpcPojo().getId() + ":" + i;
      if (action.getDelay() > 0
          && now - lastClicked.getOrDefault(key, Long.MIN_VALUE / 2) < action.getFixedDelay())
        continue;
      lastClicked.put(key, now);
      action.run(this, action.getAction());
    }
  }

  public UUID getUUID() {
    return uuid;
  }

  public GameProfile getGameProfile() {
    return gameProfile;
  }

  public Object getPlayerConnection() {
    return playerConnection;
  }

  public boolean isHasPath() {
    return hasPath;
  }

  public void setHasPath(boolean b) {
    hasPath = b;
  }

  public List<EventService<?>> getEventServices() {
    return eventServices;
  }

  public Player toPlayer() {
    return Bukkit.getPlayer(uuid);
  }

  public static ZUser find(UUID uuid) {
    return USER_MAP.computeIfAbsent(uuid, ZUser::new);
  }

  public static ZUser find(Player player) {
    return find(player.getUniqueId());
  }

  public static void unregister(Player player) {
    ZUser user = USER_MAP.remove(player.getUniqueId());
    if (user == null) return;
    NPC.all().forEach(n -> n.delete(user));
    user.channel
        .eventLoop()
        .execute(
            () -> {
              if (user.channel.pipeline().get(CHANNEL_NAME) != null)
                user.channel.pipeline().remove(CHANNEL_NAME);
            });
  }
}
