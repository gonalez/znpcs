package io.github.znetworkw.znpcservers.npc.packet;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that the return value of a {@link Packet}
 * method should be cached.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PacketValue {
    /**
     * Returns the key name for the cache.
     *
     * @return The key name for the cache.
     */
    String keyName();

    /**
     * Returns the value type.
     *
     * @return The key value type.
     */
    ValueType valueType() default ValueType.DEFAULT;
}
