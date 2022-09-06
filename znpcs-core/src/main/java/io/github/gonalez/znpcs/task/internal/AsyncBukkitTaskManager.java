package io.github.gonalez.znpcs.task.internal;

import io.github.gonalez.znpcs.ZNPCs;
import io.github.gonalez.znpcs.task.Task;
import io.github.gonalez.znpcs.task.TaskManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class AsyncBukkitTaskManager extends BukkitRunnable implements TaskManager {
    private final Set<Task> submittedTasks = new LinkedHashSet<>();

    private final int time;

    private boolean started = false;

    public AsyncBukkitTaskManager(int runTime) {
        this.time = runTime;
    }

    @Override
    public void run() {
        for (Task task : submittedTasks) {
            if (task.isCanceled()) {
                removeTask(task);
            }

            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        if (isStarted()) {
            throw new IllegalStateException("this task is already started.");
        }

        started = true;
        ZNPCs.SCHEDULER.runTaskTimerAsynchronously(this, time, time);
    }

    @Override
    public Task submitTask(Task task) {
        submittedTasks.add(task);
        return task;
    }

    @Override
    public Task removeTask(Task task) {
        submittedTasks.remove(task);
        return task;
    }

    @Override
    public Iterable<Task> getTasks() {
        return submittedTasks;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean isRunning() {
        boolean running = false;
        for (Task task : submittedTasks) {
            if (task.isCanceled()) {
                running = true;
                break;
            }
        }
        return running;
    }

    @Override
    public boolean isCanceled() {
        return !isCancelled();
    }
}
