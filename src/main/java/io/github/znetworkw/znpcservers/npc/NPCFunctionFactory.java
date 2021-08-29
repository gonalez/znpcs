package io.github.znetworkw.znpcservers.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.znetworkw.znpcservers.npc.function.GlowFunction;

import static io.github.znetworkw.znpcservers.utility.GuavaCollectors.*;

/** NPC Function definitions. */
public final class NPCFunctionFactory {
    /** List of functions that does not implement
     *  {@link NPCFunction#function(NPC, String)} directly. */
    public static ImmutableList<NPCFunction> WITHOUT_FUNCTION =
        ImmutableList.of(
            new NPCFunction.WithoutFunction("look"),
            new NPCFunction.WithoutFunctionSelfUpdate("holo"),
            new NPCFunction.WithoutFunctionSelfUpdate("mirror"));
    /** List of custom functions that implement
     * {@link NPCFunction#function(NPC, String)}. */
    public static ImmutableList<NPCFunction> WITH_FUNCTION =
        ImmutableList.of(new GlowFunction());
    /** List of all available npc functions. */
    public static ImmutableList<NPCFunction> ALL =
        ImmutableList.<NPCFunction>builder()
            .addAll(WITHOUT_FUNCTION)
            .addAll(WITH_FUNCTION)
            .build();
    /** Mapping for available npc functions by its {@link NPCFunction#name()}. */
    public static ImmutableMap<String, NPCFunction> BY_NAME =
        ALL.stream().collect(toImmutableMap(
            NPCFunction::name,
            function -> function));

    /**
     * Tries to locate a npc function instance by its name.
     *
     * @param name The function name.
     * @return A function instance for the given name.
     */
    public static NPCFunction findFunctionForName(String name) {
        return BY_NAME.get(name);
    }

    /**
     * Locates active functions for the given npc.
     *
     * @param npc The npc.
     */
    public static ImmutableList<NPCFunction> findFunctionsForNpc(NPC npc) {
        return ALL.stream()
            .filter(function -> isTrue(npc, function))
            .collect(toImmutableList());
    }

    /**
     * Returns the function value for the given npc
     * if it exists, false otherwise.
     *
     * @param npc The npc to find the value on.
     * @param function The function type.
     * @return The function value for the given npc if it exists, false otherwise.
     */
    public static boolean isTrue(NPC npc, NPCFunction function) {
        return npc.getNpcPojo().getFunctions().getOrDefault(function.name(), false);
    }

    /**
     * @see #isTrue(NPC, NPCFunction)
     */
    public static boolean isTrue(NPC npc, String function) {
        return isTrue(npc, findFunctionForName(function));
    }

    private NPCFunctionFactory() {}
}
