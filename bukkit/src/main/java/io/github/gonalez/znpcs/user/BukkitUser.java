package io.github.gonalez.znpcs.user;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.gonalez.znpcs.configuration.Configuration;
import io.github.gonalez.znpcs.configuration.ConfigurationProvider;
import org.bukkit.entity.Player;

public class BukkitUser extends AbstractUser {
  private final Player player;

  public BukkitUser(
      Player player,
      ConfigurationProvider configurationProvider,
      Class<? extends Configuration> definedTextConfiguration) {
    super(
        checkNotNull(player).getUniqueId(),
        configurationProvider,
        definedTextConfiguration);
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }

  @Override
  public void sendChatText(String text) {
    player.sendMessage(text);
  }
}
