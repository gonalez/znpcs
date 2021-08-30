package io.github.znetworkw.znpcservers.npc.task;

import io.github.znetworkw.znpcservers.ServersNPC;
import io.github.znetworkw.znpcservers.configuration.ConfigurationConstants;
import io.github.znetworkw.znpcservers.npc.NPC;
import io.github.znetworkw.znpcservers.npc.FunctionFactory;
import io.github.znetworkw.znpcservers.npc.conversation.ConversationModel;
import io.github.znetworkw.znpcservers.user.ZUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable task for handling the {@link NPC}s.
 */
public class NPCManagerTask extends BukkitRunnable {
    /**
     * Creates a new task. This task will handle all the {@link NPC}s.
     *
     * @param serversNPC The plugin instance.
     */
    public NPCManagerTask(ServersNPC serversNPC) {
        this.runTaskTimerAsynchronously(serversNPC, 60L, 1L);
    }

    @Override
    public void run() {
        for (NPC npc : NPC.all()) {
            final boolean hasPath = npc.getNpcPath() != null;
            if (hasPath) {
                npc.getNpcPath().handle();
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                final ZUser zUser = ZUser.find(player);
                final boolean canSeeNPC = player.getWorld() == npc.getLocation().getWorld()
                    && player.getLocation().distance(npc.getLocation()) <= ConfigurationConstants.VIEW_DISTANCE;
                if (npc.getViewers().contains(zUser) && !canSeeNPC) // delete the npc for the player if player is not in range
                    npc.delete(zUser);
                else if (canSeeNPC) {
                    if (!npc.getViewers().contains(zUser)) {
                        npc.spawn(zUser);
                    }
                    if (FunctionFactory.isTrue(npc, "look") && !hasPath) { // look npc at player
                        npc.lookAt(zUser, player.getLocation(), false);
                    }
                    npc.getHologram().updateNames(zUser);
                    // handle npc conversation
                    ConversationModel conversationStorage = npc.getNpcPojo().getConversation();
                    if (conversationStorage != null && conversationStorage.getConversationType() == ConversationModel.ConversationType.RADIUS) {
                        npc.tryStartConversation(player);
                    }
                }
            }
        }
    }
}
