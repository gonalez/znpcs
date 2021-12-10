package io.github.znetworkw.znpcservers.entity;

import com.google.common.collect.ImmutableList;
import io.github.znetworkw.znpcservers.entity.internal.DefaultPluginEntityTransformer;
import org.bukkit.entity.Player;

/**
 * This transformer allows
 *
 * @param <T> the type being transformed.
 */
@FunctionalInterface
public interface PluginEntityTypeTransformer<T> {
    static PluginEntityTypeTransformer<Object> of() {
        return DefaultPluginEntityTransformer.INSTANCE;
    }

    /**
     *
     * @param data the data to transform.
     * @return
     * @throws Exception if cannot transform.
     */
    T transform(PluginEntityTypeData data, Object... args) throws Exception;
}
