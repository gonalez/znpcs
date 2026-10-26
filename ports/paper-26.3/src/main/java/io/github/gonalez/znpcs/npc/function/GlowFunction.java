/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.npc.function;

import io.github.gonalez.znpcs.npc.*;
import java.util.Locale;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.TeamColor;

public class GlowFunction extends NPCFunction {
  public GlowFunction() {
    super("glow");
  }

  @Override
  protected ResultType runFunction(NPC npc, FunctionContext context) {
    if (!(context instanceof FunctionContext.ContextWithValue v)) return ResultType.FAIL;
    String name = v.getValue();
    TeamColor color =
        TeamColor.byName(name == null || name.isBlank() ? "white" : name.toLowerCase(Locale.ROOT));
    if (color == null) return ResultType.FAIL;
    npc.getNpcPojo().setGlowName(name);
    npc.setGlowColor(color);
    ((Entity) npc.getNmsEntity()).setSharedFlag(6, !FunctionFactory.isTrue(npc, this));
    npc.getPackets().getProxyInstance().update(npc.getPackets());
    npc.deleteViewers();
    return ResultType.SUCCESS;
  }

  @Override
  protected boolean allow(NPC npc) {
    return true;
  }

  @Override
  public void resolve(NPC npc) {
    String name = npc.getNpcPojo().getGlowName();
    TeamColor c =
        TeamColor.byName(name == null || name.isBlank() ? "white" : name.toLowerCase(Locale.ROOT));
    npc.setGlowColor(c == null ? TeamColor.WHITE : c);
    ((Entity) npc.getNmsEntity()).setSharedFlag(6, FunctionFactory.isTrue(npc, this));
  }
}
