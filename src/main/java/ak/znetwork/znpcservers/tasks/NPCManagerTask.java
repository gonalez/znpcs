package ak.znetwork.znpcservers.tasks;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.ZNPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class NPCManagerTask extends BukkitRunnable {

    /**
     * The plugin instance.
     */
    private final ServersNPC serversNPC;

    /**
     * Creates a new task for all NPC.
     * This task will handle each npc.
     *
     * @param serversNPC The plugin instance.
     */
    public NPCManagerTask(ServersNPC serversNPC) {
        this.serversNPC = serversNPC;

        this.runTaskTimerAsynchronously(this.serversNPC, 60L, 1L);
    }

    @Override
    public void run() {
        for (ZNPC npc : serversNPC.getNpcManager().getNpcList()) {
            npc.handlePath();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) <= serversNPC.getViewDistance() && !npc.getViewers().contains(player))
                    npc.spawn(player);

                else if (player.getWorld() != npc.getLocation().getWorld() && npc.getViewers().contains(player) || player.getWorld() != npc.getLocation().getWorld() && npc.getViewers().contains(player) || player.getWorld() == npc.getLocation().getWorld() && player.getLocation().distance(npc.getLocation()) > serversNPC.getViewDistance() && npc.getViewers().contains(player))
                    npc.delete(player, true);

                if (npc.getViewers().contains(player) && player.getLocation().distance(npc.getLocation()) <= serversNPC.getViewDistance()) {
                    if (npc.isHasLookAt()) npc.lookAt(Optional.of(player), player.getLocation(), false);

                    npc.getHologram().updateNames(player);
                }
            }
        }
    }
}
