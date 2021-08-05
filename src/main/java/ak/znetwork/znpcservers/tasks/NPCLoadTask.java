package ak.znetwork.znpcservers.tasks;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.npc.ZNPC;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 2/8/2021
 */
public class NPCLoadTask extends BukkitRunnable {
    /**
     * How often to check if the world is ready to load the npc.
     */
    private static final int DELAY = 40; // 40 ticks = 2 Seconds

    /**
     * How many times to check if the npc is ready to load.
     */
    private static final int MAX_TRIES = 10;

    /**
     * The npc to load.
     */
    private final ZNPC npc;

    /**
     * Current try to load npc.
     */
    private int tries = 0;

    /**
     * Creates a new task for a NPC,
     * this task will handle the LOAD of the npc
     * and wait until the world is loaded to load the npc.
     *
     * @param npc The npc to load.
     */
    public NPCLoadTask(ZNPC npc) {
        this.npc = npc;
        ServersNPC.SCHEDULER.runTaskTimer(this, DELAY);
    }

    @Override
    public void run() {
        if (tries++ > MAX_TRIES) {
            cancel();
            return;
        }
        World world = Bukkit.getWorld(npc.getNpcPojo().getLocation().getWorld());
        if (world == null) {
            // world not loaded.. try again in 1 second
            return;
        }
        // cancel task
        cancel();
        // world found, load npc..
        npc.init();
    }
}
