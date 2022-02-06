package io.github.gonalez.znpcservers.npc.function;

import io.github.gonalez.znpcservers.npc.Npc;
import io.github.gonalez.znpcservers.npc.NpcModel;
import io.github.gonalez.znpcservers.npc.internal.function.DefaultNpcContextBuilder;

/**
 * Defines the interface by which an {@link AbstractNpcFunction} executes functions for an npc.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
 */
public interface NpcFunction {
    /**
     * The unique function name.
     *
     * @return the unique function name.
     */
    String getName();

    /**
     * Executes the function with the provided context for the npc.
     *
     * @param npc the npc to run that function for.
     * @param functionContext the function context.
     */
    void executeFunction(Npc npc, NpcFunctionContext functionContext);

    /**
     * Executes the function using the {@link #resolve(Npc)} context for the npc.
     *
     * @param npc the npc to run that function for.
     * @see #resolve(Npc)
     */
    default void executeFunction(Npc npc) {
        executeFunction(npc, resolve(npc));
    }

    /**
     * Resolves the function context for the given npc.
     * <p>
     * This method auto resolves the context for the {@link #executeFunction(Npc, NpcFunctionContext)}
     * method if required. Typically the context should be created from the information stored in
     * the npc {@link NpcModel npc model}.
     *
     * @param npc the npc to run the function for.
     * @return a function context for this function referenced from the given npc.
     */
    default NpcFunctionContext resolve(Npc npc) {
        return new DefaultNpcContextBuilder().build();
    }
}
