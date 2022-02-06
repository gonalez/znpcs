package io.github.znetworkw.znpcservers.context;

/**
 * A context that provides access of objects for a type.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface PluginContext {
    /**
     * Returns an object of the given type, or {@code null} if no object
     * is found for the given type.
     *
     * @param type the type of object to get.
     * @param <T> the type of object to get.
     * @return
     *    an object of the given type, or {@code null} if no object
     *    is found for the given type.
     */
    <T> T get(Class<T> type);

    /**
     * Returns {@code true} if the context has an object for the given type.
     *
     * @param type the type of object to check.
     * @param <T> the type of object to check.
     * @return {@code true} if the context has an object for the given type.
     */
    <T> T has(Class<T> type);
}
