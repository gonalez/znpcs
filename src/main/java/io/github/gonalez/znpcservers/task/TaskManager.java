package io.github.gonalez.znpcservers.task;

import io.github.gonalez.znpcservers.task.internal.AsyncBukkitTaskManager;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface TaskManager {
    static TaskManager of(int time) {
        return new AsyncBukkitTaskManager(time);
    }

    void start();

    /**
     * @param task the task to submit.
     * @return submitted task.
     */
    Task submitTask(Task task);

    /**
     *
     * @param task the task to remove.
     * @return removed task.
     */
    Task removeTask(Task task);

    Iterable<Task> getTasks();

    boolean isStarted();
    boolean isRunning();
    boolean isCanceled();
}
