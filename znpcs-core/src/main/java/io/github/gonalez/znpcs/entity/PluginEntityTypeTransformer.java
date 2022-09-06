package io.github.gonalez.znpcs.entity;

import io.github.gonalez.znpcs.entity.internal.DefaultPluginEntityTransformer;

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
