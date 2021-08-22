package io.github.znetworkw.znpcservers.utility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Helper functions for the bukkit scheduler.
 */
public class SchedulerUtils {
    /** The plugin instance. */
    private final Plugin plugin;

    /**
     * Initializes the scheduler utils for the plugin.
     *
     * @param plugin The plugin instance.
     */
    public SchedulerUtils(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Schedules a new repeated runnable.
     *
     * @param bukkitRunnable The runnable instance to execute.
     * @param delay The delay to wait before executing the runnable.
     * @return The bukkit task for the runnable.
     */
    public BukkitTask runTaskTimer(BukkitRunnable bukkitRunnable,
                                        int delay) {
        return runTaskTimer(bukkitRunnable, delay, delay);
    }

    /**
     * Schedules a new repeated runnable.
     *
     * @param bukkitRunnable The runnable instance to execute.
     * @param delay The first delay to wait before executing the runnable.
     * @param continuousDelay The delay to wait before execute the runnable.
     * @return  The bukkit task for the runnable.
     */
    public BukkitTask runTaskTimer(BukkitRunnable bukkitRunnable,
                                   int delay,
                                   int continuousDelay) {
        return bukkitRunnable.runTaskTimer(plugin, delay, continuousDelay);
    }

    /**
     * Schedules a new repeated asynchronous task.
     *
     * @param runnable The runnable to execute.
     * @param delay The first delay to wait before executing the runnable.
     * @param continuousDelay The delay to wait before execute the runnable.
     * @return The bukkit task for the runnable.
     */
    public BukkitTask runTaskTimerAsynchronously(Runnable runnable,
                                                 int delay,
                                                 int continuousDelay) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
                runnable,
                delay,
                continuousDelay
        );
    }

    /**
     * Schedules a new runnable at a later time.
     *
     * @param runnable The runnable to execute.
     * @param delay The delay to wait before execute the runnable.
     */
    public void scheduleSyncDelayedTask(Runnable runnable, int delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin,
                runnable,
                delay
        );
    }

    /**
     * Schedules a new runnable.
     *
     * @param runnable The runnable to execute.
     * @return The bukkit task for the runnable.
     */
    public BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
