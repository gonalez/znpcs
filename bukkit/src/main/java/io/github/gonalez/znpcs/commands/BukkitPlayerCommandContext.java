package io.github.gonalez.znpcs.commands;

import io.github.gonalez.znpcs.command.CommandContext;
import org.bukkit.entity.Player;

public class BukkitPlayerCommandContext implements CommandContext {
  private final Player player;

  public BukkitPlayerCommandContext(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public String getId() {
    return player.getUniqueId().toString();
  }

  @Override
  public void log(String message, Object... args) {
    player.sendMessage(String.format(message, args));
  }
}
