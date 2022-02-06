package io.github.gonalez.znpcservers.npc.function;

import io.github.gonalez.znpcservers.npc.internal.function.DefaultNpcFunctionRegistry;

/**
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcFunctionRegistry {
    static NpcFunctionRegistry of() {
        return new DefaultNpcFunctionRegistry();
    }

    /**
     * Retrieves the npc function in this registry for the specified name.
     *
     * @param name the npc function name.
     * @return
     *    the npc function matching the specified name, or
     *    {@code null} if no match npc function was found.
     */
    NpcFunction getFunction(String name);

    /**
     * Adds a new npc function to this registry.
     *
     * @param name the function name.
     * @param function the function to add to this registry.
     * @return the added function.
     */
    NpcFunction register(String name, NpcFunction function);

    /**
     * Resolves the name in which the given function will be registered
     * using the {@link NpcFunction#getName() npc function name}.
     *
     * @see #register(String, NpcFunction) 
     */
    default NpcFunction register(NpcFunction function) {
        return register(function.getName(), function);
    }
    
    /**
     * Unregisters a npc function in this registry.
     *
     * @param name the function name.
     * @return the removed npc function.
     */
    NpcFunction unregister(String name);

    /**
     * Retrieves all npc functions in this registry,
     * the order of iteration is undefined.
     *
     * @return all npc functions in this storage.
     */
    Iterable<NpcFunction> getFunctions();
}
