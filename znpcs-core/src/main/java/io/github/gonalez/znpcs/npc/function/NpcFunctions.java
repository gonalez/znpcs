package io.github.gonalez.znpcs.npc.function;

import static io.github.gonalez.znpcs.utility.GuavaCollectors.toImmutableList;
import static io.github.gonalez.znpcs.utility.GuavaCollectors.toImmutableMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import io.github.gonalez.znpcs.npc.Npc;
import io.github.gonalez.znpcs.npc.internal.function.SelfUpdateSavedNpcFunction;
import io.github.gonalez.znpcs.npc.internal.plugin.GlowNpcFunction;

/**
 * Custom definitions of {@link NpcFunction}s.
 *
 * @author Gaston Gonzalez {@literal <znetworkw.dev@gmail.com>}
*/
public final class NpcFunctions {
    /**
     * List of custom NPC functions that does not implement a function.
     */
    public static final ImmutableList<NpcFunction> WITHOUT_FUNCTION =
        ImmutableList.of(
            new SelfUpdateSavedNpcFunction("look"),
            new SelfUpdateSavedNpcFunction("holo"),
            new SelfUpdateSavedNpcFunction("mirror"));
    /**
     * List of custom NPC functions that implement a function.
     */
    public static final ImmutableList<NpcFunction> WITH_FUNCTION =
        ImmutableList.of(new GlowNpcFunction());
    /**
     * List of all allowed custom NPC functions.
     */
    public static final ImmutableList<NpcFunction> ALLOWED_FUNCTIONS =
        ImmutableList.<NpcFunction>builder()
            .addAll(WITHOUT_FUNCTION)
            .addAll(WITH_FUNCTION)
            .build();
    /**
     * Mapping of all available npc functions by its {@link NpcFunction#getName()}.
     */
    public static final ImmutableMap<String, NpcFunction> BY_NAME =
        ALLOWED_FUNCTIONS.stream().collect(toImmutableMap(
            NpcFunction::getName, function -> function));

    /**
     * Returns the function corresponding to the specified name or {@code null}
     * if no such function exists matching the specified name.
     *
     * @param name the function name.
     * @return
     *    the function with the given name
     *    or {@code null} if the function does not exists.
     */
    public static NpcFunction findFunctionForName(String name) {
        return BY_NAME.get(name);
    }

    /**
     * Returns a list of active functions for the specified npc.
     *
     * @param npc the npc.
     * @return a list of active functions for the specified npc.
     * @see #isTrue(Npc, NpcFunction)
     */
    public static ImmutableList<NpcFunction> findFunctionsForNpc(Npc npc) {
        return ALLOWED_FUNCTIONS.stream().filter(function -> isTrue(npc, function)).collect(toImmutableList());
    }

    /**
     * Returns the value corresponding to the given function, false otherwise.
     *
     * @param npc the npc to find the value on.
     * @param function the function.
     * @return the value corresponding to the given function, false otherwise.
     */
    public static boolean isTrue(Npc npc, NpcFunction function) {
        final NpcFunctionModel functionModel = npc.getModel().getFunctions().get(function.getName());
        return functionModel != null && functionModel.isEnabled();
    }

    /**
     * Resolves the function for the specified name.
     * @see #isTrue(Npc, NpcFunction)
     */
    public static boolean isTrue(Npc npc, String function) {
        return isTrue(npc, findFunctionForName(function));
    }

    private NpcFunctions() {}
}
