package io.github.gonalez.znpcs.npc.task;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.NPCSkin;
import io.github.gonalez.znpcs.skin.ApplySkinFetcherListener;
import io.github.gonalez.znpcs.skin.SkinFetcher;
import io.github.gonalez.znpcs.skin.SkinFetcherListener;
import io.github.gonalez.znpcs.utility.PlaceholderUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class NpcRefreshSkinTask extends BukkitRunnable {
  private final Set<Integer> outgoingRefresh = new HashSet<>();
  private final SkinFetcher skinFetcher;

  private int count = 0;

  public NpcRefreshSkinTask(SkinFetcher skinFetcher) {
    this.skinFetcher = Preconditions.checkNotNull(skinFetcher);
  }

  @Override
  public void run() {
    for (NPC npc : NPC.all()) {
      int refreshSkinDuration = npc.getNpcPojo().getRefreshSkinDuration();
      if (refreshSkinDuration != 0 && count % refreshSkinDuration == 0) {
        int id = npc.getNpcPojo().getId();
        if (outgoingRefresh.contains(id)) {
          continue;
        }
        outgoingRefresh.add(id);
        skinFetcher.fetchGameProfile(
            PlaceholderUtils.formatPlaceholders(npc.getNpcPojo().getSkinName()),
            new ApplySkinFetcherListener(npc) {
              @Override
              public void onComplete(GameProfile gameProfile) {
                outgoingRefresh.remove(id);
                super.onComplete(gameProfile);
              }

              @Override
              public void onError(Throwable error) {
                outgoingRefresh.remove(id);
                super.onError(error);
              }
            });
      }
    }
    count++;
  }
}
