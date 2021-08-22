package io.github.znetworkw.znpcservers.tasks;

import io.github.znetworkw.znpcservers.ServersNPC;
import io.github.znetworkw.znpcservers.configuration.Config;
import io.github.znetworkw.znpcservers.npc.NPC;
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
        Config.SAVE_CONFIGURATIONS.forEach(Config::save);
    }
}
