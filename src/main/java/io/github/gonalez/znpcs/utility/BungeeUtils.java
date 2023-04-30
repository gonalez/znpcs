package io.github.gonalez.znpcs.utility;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BungeeUtils {
  private final Plugin plugin;
  
  public BungeeUtils(Plugin plugin) {
    this.plugin = plugin;
  }
  
  public void sendPlayerToServer(Player player, String server) {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(b);
    try {
      out.writeUTF("Connect");
      out.writeUTF(server);
    } catch (IOException e) {
      e.printStackTrace();
    } 
    player.sendPluginMessage(this.plugin, "BungeeCord", b.toByteArray());
  }
}
