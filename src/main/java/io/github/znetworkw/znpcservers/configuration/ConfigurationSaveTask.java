package io.github.znetworkw.znpcservers.configuration;

import io.github.znetworkw.znpcservers.ZNPCs;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable task for saving plugin configurations.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class ConfigurationSaveTask extends BukkitRunnable {
    /**
     * Creates a new task. This task will save all plugin configurations.
     */
    public ConfigurationSaveTask(int time) {
        ZNPCs.SCHEDULER.runTaskTimer(this, 300, time);
    }

    @Override
    public void run() {
        Configuration.SAVE_CONFIGURATIONS.forEach(Configuration::save);
    }
}