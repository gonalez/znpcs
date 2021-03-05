package ak.znetwork.znpcservers.utility;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import lombok.Getter;

/**
 * <p>Copyright (c) ZNetwork, 2020.</p>
 *
 * @author ZNetwork
 * @since 07/02/2020
 */
@Getter
public class SchedulerUtils {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Initializes the scheduler utils.
     *
     * @param plugin The plugin instance.
     */
    public SchedulerUtils(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Schedules a runnable at a later time.
     *
     * @param runnable The runnable to execute.
     * @param delay    The delay for the runnable to be executed
     */
    public void scheduleSyncDelayedTask(Runnable runnable, int delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(),
                runnable,
                delay
        );
    }
}
