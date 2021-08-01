package ak.znetwork.znpcservers.tasks;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.ZNPC;
import ak.znetwork.znpcservers.types.ConfigTypes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public class NPCManagerTask extends BukkitRunnable {
    /**
     * Creates a new task for all NPC.
     * This task will handle each npc.
     *
     * @param serversNPC The plugin instance.
     */
    public NPCManagerTask(ServersNPC serversNPC) {
        this.runTaskTimerAsynchronously(serversNPC, 60L, 1L);
    }

    @Override
    public void run() {
        for (ZNPC npc : ZNPC.all()) {
            boolean hasPath = npc.getNpcPath() != null;
            // Manage npc path
            if (hasPath) {
                npc.getNpcPath().handle();
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean canSeeNPC = player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) <= ConfigTypes.VIEW_DISTANCE;
                if (npc.getViewers().contains(player) && !canSeeNPC)
                    // Delete npc if player is not in range
                    npc.delete(player, true);
                else if (canSeeNPC) {
                    // Update npc for player
                    if (!npc.getViewers().contains(player)) {
                        npc.spawn(player);
                    }
                    // Look npc at player
                    if (npc.getNpcPojo().isHasLookAt() && !hasPath) {
                        npc.lookAt(player, player.getLocation(), false);
                    }
                    // Update hologram lines for player
                    npc.getHologram().updateNames(player);
                }
            }
        }
    }
}
