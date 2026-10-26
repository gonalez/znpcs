/* ZNPCs 5.3 compatibility port. Apache-2.0; see LICENSE and NOTICE. */
package io.github.gonalez.znpcs.npc.task;

import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCSkin;
import io.github.gonalez.znpcs.skin.*;
import io.github.gonalez.znpcs.utility.PlaceholderUtils;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.scheduler.BukkitRunnable;

/** Restore the periodic skin refresh using the same provider service as /znpcs skin. */
public class NpcRefreshSkinTask extends BukkitRunnable {
  private final SkinFetcher fetcher;
  private final Set<Integer> pending = new HashSet<>();
  private long count;

  public NpcRefreshSkinTask() {
    this(ServersNPC.getPlugin(ServersNPC.class).getSkinFetcher());
  }

  public NpcRefreshSkinTask(SkinFetcher fetcher) {
    this.fetcher = fetcher;
  }

  @Override
  public void run() {
    for (NPC npc : NPC.all()) {
      int duration = npc.getNpcPojo().getRefreshSkinDuration();
      int id = npc.getNpcPojo().getId();
      if (duration <= 0 || count % duration != 0 || !pending.add(id)) continue;
      String name = PlaceholderUtils.formatPlaceholders(npc.getNpcPojo().getSkinName());
      fetcher.fetchSkin(
          name,
          new SkinFetcher.SkinFetchListener() {
            @Override
            public void onSuccess(SkinFetcherServer server, SkinProperties properties) {
              pending.remove(id);
              if (NPC.find(id) == npc)
                npc.changeSkin(
                    NPCSkin.forValues(properties.getValue(), properties.getSignature()), true);
            }

            @Override
            public void onError(Throwable error) {
              pending.remove(id);
            }
          });
    }
    count++;
  }
}
