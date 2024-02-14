package io.github.gonalez.znpcs.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.gonalez.znpcs.npc.function.GlowFunction;
import io.github.gonalez.znpcs.utility.GuavaCollectors;

public final class FunctionFactory {
  public static ImmutableList<NPCFunction> WITHOUT_FUNCTION = ImmutableList.of(new NPCFunction.WithoutFunction("look"), new NPCFunction.WithoutFunctionSelfUpdate("holo"), new NPCFunction.WithoutFunctionSelfUpdate("mirror"));
  
  public static ImmutableList<NPCFunction> WITH_FUNCTION = ImmutableList.of(new GlowFunction());
  
  public static ImmutableList<NPCFunction> ALL =
      ImmutableList.<NPCFunction>builder()
          .addAll(WITHOUT_FUNCTION)
          .addAll(WITH_FUNCTION)
          .build();
  
  public static ImmutableMap<String, NPCFunction> BY_NAME;
  
  static {
    BY_NAME = ALL.stream().collect(GuavaCollectors.toImmutableMap(NPCFunction::getName, function -> function));
  }
  
  public static NPCFunction findFunctionForName(String name) {
    return BY_NAME.get(name);
  }
  
  public static ImmutableList<NPCFunction> findFunctionsForNpc(NPC npc) {
    return ALL.stream()
      .filter(function -> isTrue(npc, function))
      .collect(GuavaCollectors.toImmutableList());
  }
  
  public static boolean isTrue(NPC npc, NPCFunction function) {
    return npc.getNpcPojo().getFunctions().getOrDefault(function.getName(), Boolean.FALSE);
  }
  
  public static boolean isTrue(NPC npc, String function) {
    return isTrue(npc, findFunctionForName(function));
  }
}
