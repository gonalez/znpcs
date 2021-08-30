package io.github.znetworkw.znpcservers.npc.task;

import io.github.znetworkw.znpcservers.ServersNPC;
import io.github.znetworkw.znpcservers.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable task for loading a {@link NPC}.
 */
public class NPCLoadTask extends BukkitRunnable {
    /** How often to check if the world is ready to load the npc. */
    private static final int DELAY = 40; // 40 ticks = 2 seconds
    /** How many times to check if the npc can be load. */
    private static final int MAX_TRIES = 10;

    /** The npc to load. */
    private final NPC npc;
    /** Current try to load npc. */
    private int tries = 0;

    /**
     * Creates a new task for an NPC. This task will wait until the world is loaded
     * to load the npc.
     *
     * @param npc The npc to load.
     */
    public NPCLoadTask(NPC npc) {
        this.npc = npc;
        ServersNPC.SCHEDULER.runTaskTimer(this, DELAY);
    }

    @Override
    public void run() {
        if (tries++ > MAX_TRIES) {
            cancel();
            return;
        }
        World world = Bukkit.getWorld(npc.getNpcPojo().getLocation().getWorldName());
        if (world == null) { // world is not loaded..
            return;
        }
        // cancel task
        cancel();
        npc.onLoad(); // world found, load npc..
    }
}
