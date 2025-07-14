package io.github.gonalez.znpcs.user;

import com.mojang.authlib.GameProfile;
import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCAction;
import io.github.gonalez.znpcs.npc.event.ClickType;
import io.github.gonalez.znpcs.npc.event.NPCInteractEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ZUser {
  private static final String CHANNEL_NAME = "npc_interact";
  
  private static final int DEFAULT_DELAY = 1;
  
  private static final Map<UUID, ZUser> USER_MAP = new HashMap<>();
  
  private final Map<Integer, Long> lastClicked;
  
  private final List<EventService<?>> eventServices;
  
  private final UUID uuid;
  
  private final GameProfile gameProfile;
  
  private final Object playerConnection;
  
  private boolean hasPath = false;
  
  private long lastInteract = 0L;
  
  public ZUser(UUID uuid) {
    this.uuid = uuid;
    this.lastClicked = new HashMap<>();
    this.eventServices = new ArrayList<>();
    try {
      Object playerHandle = CacheRegistry.GET_HANDLE_PLAYER_METHOD.load().invoke(toPlayer());
      this.gameProfile = (GameProfile) CacheRegistry.GET_PROFILE_METHOD.load().invoke(playerHandle, new Object[0]);
      Channel channel = (Channel) CacheRegistry.CHANNEL_FIELD.load().get(CacheRegistry.NETWORK_MANAGER_FIELD.load()
          .get(this.playerConnection = CacheRegistry.PLAYER_CONNECTION_FIELD.load().get(playerHandle)));
      if (channel.pipeline().names().contains("npc_interact"))
        channel.pipeline().remove("npc_interact"); 
      channel.pipeline().addAfter("decoder", "npc_interact", new NpcInteractServerHandler(this));
    } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
      throw new IllegalStateException("can't create player " + uuid.toString(), e.getCause());
    } 
  }
  
  public UUID getUUID() {
    return this.uuid;
  }
  
  public GameProfile getGameProfile() {
    return this.gameProfile;
  }
  
  public Object getPlayerConnection() {
    return this.playerConnection;
  }
  
  public boolean isHasPath() {
    return this.hasPath;
  }
  
  public List<EventService<?>> getEventServices() {
    return this.eventServices;
  }
  
  public void setHasPath(boolean hasPath) {
    this.hasPath = hasPath;
  }
  
  public Player toPlayer() {
    return Bukkit.getPlayer(this.uuid);
  }
  
  public static ZUser find(UUID uuid) {
    return USER_MAP.computeIfAbsent(uuid, ZUser::new);
  }
  
  public static ZUser find(Player player) {
    return find(player.getUniqueId());
  }
  
  public static void unregister(Player player) {
    ZUser zUser = USER_MAP.get(player.getUniqueId());
    if (zUser == null)
      throw new IllegalStateException("can't find user " + player.getUniqueId()); 
    USER_MAP.remove(player.getUniqueId());
    NPC.all().stream()
      .filter(npc -> npc.getViewers().contains(zUser))
      .forEach(npc -> npc.delete(zUser));
  }
}
