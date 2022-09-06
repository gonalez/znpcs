package io.github.gonalez.znpcs.npc.function;

import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.npc.internal.function.DefaultNpcContextBuilder;

import java.util.Map;

/**
 * Context interface that defines attributes that will be used when invoking
 * a function using {@link AbstractNpcFunction#function(Npc, NpcFunctionContext)}.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcFunctionContext {
    /**
     * An null {@link NpcFunctionContext}.
     */
    NpcFunctionContext NULL_CONTEXT = () -> null;

    static FunctionContextBuilder builder() {
        return new DefaultNpcContextBuilder();
    }

    /**
     * Returns a map of all attributes that were supplied when creating the context.
     *
     * @return a map of all attributes.
     */
    Map<String, Object> attributes();

    /**
     * Returns the attribute of the context with the specified name,
     * or {@code null} if no attribute exists.
     */
    default Object getAttribute(String name) {
        return attributes().get(name);
    }

    /**
     * A builder to create a {@link NpcFunctionContext} from a list of attributes.
     */
    interface FunctionContextBuilder {
        /**
         * Sets a attribute for the given name. If a previous attribute exists for
         * the given name, it will be replaced by the given singleton.
         *
         * @return this.
         */
        FunctionContextBuilder addAttribute(String string, Object object);

        /**
         * Merges the provided attributes to the this builder attributes.
         *
         * @return this.
         */
        FunctionContextBuilder merge(Map<String, Object> map);

        /**
         * A {@link NpcFunctionContext} based from this builder.
         *
         * @return A new {@link NpcFunctionContext} based from this builder.
         */
        NpcFunctionContext build();
    }
}
