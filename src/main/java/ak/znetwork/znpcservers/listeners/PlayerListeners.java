package ak.znetwork.znpcservers.listeners;

import ak.znetwork.znpcservers.ServersNPC;

import ak.znetwork.znpcservers.manager.NPCManager;
import ak.znetwork.znpcservers.types.ConfigTypes;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class PlayerListeners implements Listener {

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
        serversNPC.setupNetty(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        NPCManager.getNpcUsers().stream().filter(playerNetty -> playerNetty.getUuid() == event.getPlayer().getUniqueId()).findFirst().ifPresent(playerNetty -> {
            playerNetty.ejectNetty();

            NPCManager.getNpcUsers().remove(playerNetty);
        });

        ConfigTypes.NPC_LIST.stream().filter(npc -> npc.getViewers().contains(event.getPlayer())).forEach(npc -> npc.delete(event.getPlayer(), true));
    }
}
