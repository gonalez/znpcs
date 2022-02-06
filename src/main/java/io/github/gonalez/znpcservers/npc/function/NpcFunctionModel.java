package io.github.gonalez.znpcservers.npc.function;

import io.github.gonalez.znpcservers.npc.Npc;
import io.github.gonalez.znpcservers.npc.NpcModel;

import java.util.Map;

/**
 * Specifies the model of an {@link AbstractNpcFunction} must be saved only
 * if {@link AbstractNpcFunction#isSaveAllowed() save allowed} when calling
 * {@link NpcFunction#executeFunction(Npc, NpcFunctionContext)}, usually saved in the npc {@link NpcModel}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class NpcFunctionModel {
    /**
     * Used to check if the function should be used or not.
     */
    private final boolean enabled;

    /**
     * Represents the list of attributes for the provided
     * context when creating a new instance.
     */
    private final Map<String, Object> attributes;

    /**
     * Constructs a new {@link NpcFunctionModel} for the specified context and toggle value.
     *
     * @param functionContext the context.
     * @param enabled the toggle value.
     */
    public NpcFunctionModel(NpcFunctionContext functionContext, boolean enabled) {
        this.enabled = enabled;
        this.attributes = functionContext.attributes();
    }

    /**
     * Returns {@code true} if this model is enabled.
     *
     * @return {@code true} if this model is enabled.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * A list of attributes referenced by the given context when creating the instance.
     *
     * @return a list of attributes referenced by the given context.
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
