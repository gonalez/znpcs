package ak.znetwork.znpcservers.tasks;

import ak.znetwork.znpcservers.ServersNPC;
import ak.znetwork.znpcservers.configuration.ZNConfig;
import ak.znetwork.znpcservers.manager.ConfigManager;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * A Task to save NPCs.
 *
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
public final class NPCSaveTask extends BukkitRunnable {

    /**
     * Initialization of the task to save NPCs.
     *
     * @param serversNPC The plugin instance.
     * @param seconds How often the npc will be saved (in seconds).
     */
    public NPCSaveTask(ServersNPC serversNPC,
                       int seconds) {
        this.runTaskTimer(serversNPC, 100L, seconds);
    }

    @Override
    public void run() {
        ConfigManager.all().forEach(ZNConfig::save);
    }
}
