package ak.znetwork.znpcservers.tasks;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.Config;
import ak.znetwork.znpcservers.manager.ConfigManager;
import ak.znetwork.znpcservers.npc.NPC;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable task for saving the created {@link NPC}s.
 */
public class NPCSaveTask extends BukkitRunnable {
    /**
     * Creates a new task. This task will handle the saving of
     * all created {@link NPC}s.
     *
     * @param serversNPC The plugin instance.
     * @param seconds How often to save.
     */
    public NPCSaveTask(ServersNPC serversNPC,
                       int seconds) {
        this.runTaskTimer(serversNPC, 200L, seconds);
    }

    @Override
    public void run() {
        ConfigManager.all().forEach(Config::save);
    }
}
