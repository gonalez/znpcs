package ak.znetwork.znpcservers.listeners;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.types.ConfigTypes;
import ak.znetwork.znpcservers.user.ZNPCUser;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PlayerListeners implements Listener {

    /**
     * The plugin instance.
     */
    private final ServersNPC serversNPC;

    /**
     * Creates and register the necessary events for players.
     *
     * @param serversNPC The plugin instance.
     */
    public PlayerListeners(ServersNPC serversNPC) {
        this.serversNPC = serversNPC;

        this.serversNPC.getServer().getPluginManager().registerEvents(this, serversNPC);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ZNPCUser.registerOrGet(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ZNPCUser.unregister(event.getPlayer().getUniqueId());

        ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getViewers().contains(event.getPlayer())).forEach(npc -> npc.delete(event.getPlayer(), true));
    }
}
