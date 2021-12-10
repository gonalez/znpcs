package io.github.znetworkw.znpcservers.utility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Helper functions for the bukkit scheduler.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class SchedulerUtils {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Initializes the scheduler utils for the plugin.
     *
     * @param plugin the plugin instance.
     */
    public SchedulerUtils(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Schedules a new repeated runnable.
     *
     * @param bukkitRunnable the runnable instance to execute.
     * @param delay the delay to wait before executing the runnable.
     * @return the bukkit task for the runnable.
     */
    public BukkitTask runTaskTimer(BukkitRunnable bukkitRunnable, int delay) {
        return runTaskTimer(bukkitRunnable, delay, delay);
    }

    /**
     * Schedules a new repeated runnable.
     *
     * @param bukkitRunnable the runnable instance to execute.
     * @param delay the first delay to wait before executing the runnable.
     * @param continuousDelay the delay to wait before execute the runnable.
     * @return the bukkit task for the runnable.
     */
    public BukkitTask runTaskTimer(BukkitRunnable bukkitRunnable, int delay, int continuousDelay) {
        return bukkitRunnable.runTaskTimer(plugin, delay, continuousDelay);
    }

    /**
     * Schedules a new repeated asynchronous task.
     *
     * @param runnable the runnable to execute.
     * @param delay the first delay to wait before executing the runnable.
     * @param continuousDelay the delay to wait before execute the runnable.
     * @return the bukkit task for the runnable.
     */
    public BukkitTask runTaskTimerAsynchronously(
            BukkitRunnable runnable, int delay, int continuousDelay) {
        return runnable.runTaskTimerAsynchronously(plugin, delay, continuousDelay);
    }

    /**
     * Schedules a new repeated asynchronous task.
     *
     * @param runnable the runnable to execute.
     * @param delay the first delay to wait before executing the runnable.
     * @param continuousDelay the delay to wait before execute the runnable.
     * @return the bukkit task for the runnable.
     */
    public BukkitTask runTaskTimerAsynchronously(
        Runnable runnable, int delay, int continuousDelay) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, continuousDelay);
    }

    /**
     * Schedules a new runnable at a later time.
     *
     * @param runnable the runnable to execute.
     * @param delay the delay to wait before execute the runnable.
     */
    public void scheduleSyncDelayedTask(Runnable runnable, int delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay);
    }

    /**
     * Schedules a new runnable.
     *
     * @param runnable the runnable to execute.
     * @return the bukkit task for the runnable.
     */
    public BukkitTask runTask(Runnable runnable) {
        return Bukkit.getScheduler().runTask(plugin, runnable);
    }
}
