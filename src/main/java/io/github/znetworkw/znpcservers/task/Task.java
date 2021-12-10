package io.github.znetworkw.znpcservers.task;

import io.github.znetworkw.znpcservers.task.internal.CancelableTask;
import io.github.znetworkw.znpcservers.task.internal.DefaultTask;

import java.util.concurrent.Callable;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface Task extends Runnable {
    static Task of(Runnable runnable) {
        return new DefaultTask(runnable);
    }

    static Task of(Runnable runnable, Callable<Boolean> callable) {
        return new CancelableTask(runnable, callable);
    }

    @Override
    void run();

    boolean cancel();
    boolean isCanceled();
}
