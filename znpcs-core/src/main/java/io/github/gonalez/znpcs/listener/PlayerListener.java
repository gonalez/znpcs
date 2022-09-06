package io.github.gonalez.znpcs.listener;

import io.github.gonalez.znpcs.ZNPCs;
import io.github.gonalez.znpcs.user.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            ZNPCs.SETTINGS.getUserStore().addUser(User.of(event.getPlayer()));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ZNPCs.SETTINGS.getUserStore().removeUser(event.getPlayer().getUniqueId());
    }
}
