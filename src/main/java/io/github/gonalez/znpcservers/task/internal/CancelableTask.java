package io.github.gonalez.znpcservers.task.internal;

import java.util.concurrent.Callable;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class CancelableTask extends DefaultTask {
    private final Callable<Boolean> callable;

    public CancelableTask(Runnable runnable, Callable<Boolean> callable) {
        super(runnable);
        this.callable = callable;
    }

    @Override
    public boolean isCanceled() {
        try {
            return !callable.call() && super.isCanceled();
        } catch (Exception exception) {
            return true;
        }
    }
}
