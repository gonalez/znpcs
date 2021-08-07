package ak.znetwork.znpcservers.listeners;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.events.NPCInteractEvent;
import ak.znetwork.znpcservers.npc.conversation.ConversationModel;
import ak.znetwork.znpcservers.user.EventService;
import ak.znetwork.znpcservers.user.ZUser;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class PlayerListener implements Listener {
    /**
     * Creates and register the necessary events for players.
     *
     * @param serversNPC The plugin instance.
     */
    public PlayerListener(ServersNPC serversNPC) {
        serversNPC.getServer().getPluginManager().registerEvents(this, serversNPC);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        ZUser.find(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        ZUser.unregister(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onTalk(AsyncPlayerChatEvent event) {
        ZUser zUser = ZUser.find(event.getPlayer());
        if (EventService.hasService(zUser, AsyncPlayerChatEvent.class)) {
            event.setCancelled(true);
            // gui logic
            EventService<AsyncPlayerChatEvent> eventService = EventService.findService(zUser, AsyncPlayerChatEvent.class);
            eventService.getEventConsumers().forEach(consumer -> consumer.accept(event));
            // remove
            zUser.getEventServices().remove(eventService);
        }
    }

    @EventHandler
    public void onConversation(NPCInteractEvent event) {
        ConversationModel conversationStorage = event.getNpc().getNpcPojo().getConversation();
        if (conversationStorage == null || conversationStorage.getConversationType() != ConversationModel.ConversationType.CLICK) {
            return;
        }
        event.getNpc().tryStartConversation(event.getPlayer());
    }
}
