package io.github.gonalez.znpcs.npc.task;

import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCSkin;
import io.github.gonalez.znpcs.utility.PlaceholderUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class NpcRefreshSkinTask extends BukkitRunnable {
  private final Set<Integer> outgoingRefresh = new HashSet<>();

  private int count = 0;

  @Override
  public void run() {
    for (NPC npc : NPC.all()) {
      int refreshSkinDuration = npc.getNpcPojo().getRefreshSkinDuration();
      if (refreshSkinDuration != 0 && count % refreshSkinDuration == 0) {
        int id = npc.getNpcPojo().getId();
        if (outgoingRefresh.contains(id))
          continue;
        outgoingRefresh.add(id);
        NPCSkin.forName(PlaceholderUtils.formatPlaceholders(npc.getNpcPojo().getSkinName()),
            (paramArrayOfString, paramThrowable) -> {
              if (paramThrowable == null) {
                npc.changeSkin(NPCSkin.forValues(paramArrayOfString));
              }
              outgoingRefresh.remove(id);
            });
      }
    }
    count++;
  }
}
