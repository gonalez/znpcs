package io.github.znetworkw.znpcservers.packet;

import io.github.znetworkw.znpcservers.packet.internal.CachingPluginPacketConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Information for caching a packet, mark a packet for caching via the {@link CachingPluginPacketConverter}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PluginPacketInfo {
    /**
     * The key name to be used when caching the packet.
     * <p>
     * If multiple packets have the same key name, the first cached value
     * for this name will be returned.
     *
     * @return the key name or default {@code ""}.
     */
    String name() default "";
}
