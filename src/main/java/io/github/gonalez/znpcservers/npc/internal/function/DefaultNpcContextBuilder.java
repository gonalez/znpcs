package io.github.gonalez.znpcservers.npc.internal.function;

import java.util.HashMap;
import java.util.Map;

import io.github.gonalez.znpcservers.npc.function.NpcFunctionContext;

/**
 * A default implementation of {@link NpcFunctionContext.FunctionContextBuilder}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public class DefaultNpcContextBuilder implements NpcFunctionContext.FunctionContextBuilder {
    /**
     * Map of attributes for the context.
     */
    protected final Map<String, Object> attributes;

    /**
     * Creates a new {@link DefaultNpcContextBuilder} with default data.
     */
    public DefaultNpcContextBuilder() {
        this(new HashMap<>());
    }

    /**
     * Creates a new {@link DefaultNpcContextBuilder} with the given attributes.
     *
     * @param attributes map of custom attributes for the context.
     */
    protected DefaultNpcContextBuilder(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public NpcFunctionContext.FunctionContextBuilder addAttribute(String string, Object object) {
        attributes.put(string, object);
        return this;
    }

    @Override
    public NpcFunctionContext.FunctionContextBuilder merge(Map<String, Object> map) {
        attributes.putAll(map);
        return this;
    }

    @Override
    public NpcFunctionContext build() {
        return () -> attributes;
    }
}
