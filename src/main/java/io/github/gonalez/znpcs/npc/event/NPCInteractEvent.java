package io.github.gonalez.znpcs.npc.event;

import io.github.gonalez.znpcs.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends Event {
  private final Player player;
  
  private final ClickType clickType;
  
  private final NPC npc;
  
  private static final HandlerList handlerList = new HandlerList();
  
  public NPCInteractEvent(Player player, ClickType clickType, NPC npc) {
    this.player = player;
    this.clickType = clickType;
    this.npc = npc;
  }
  
  public NPCInteractEvent(Player player, String clickType, NPC npc) {
    this(player, ClickType.forName(clickType), npc);
  }
  
  public Player getPlayer() {
    return this.player;
  }
  
  public NPC getNpc() {
    return this.npc;
  }
  
  public boolean isRightClick() {
    return (this.clickType == ClickType.RIGHT);
  }
  
  public boolean isLeftClick() {
    return (this.clickType == ClickType.LEFT);
  }
  
  public HandlerList getHandlers() {
    return handlerList;
  }
  
  public static HandlerList getHandlerList() {
    return handlerList;
  }
}
