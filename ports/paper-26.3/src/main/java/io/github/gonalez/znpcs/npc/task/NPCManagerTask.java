/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.npc.task;

import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.configuration.ConfigurationConstants;
import io.github.gonalez.znpcs.npc.FunctionFactory;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.conversation.ConversationModel;
import io.github.gonalez.znpcs.user.ZUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCManagerTask extends BukkitRunnable {
  public NPCManagerTask(ServersNPC serversNPC) {
    this.runTaskTimer((Plugin) serversNPC, 60L, 1L);
  }

  public void run() {
    for (NPC npc : NPC.all()) {
      boolean hasPath;
      boolean bl = hasPath = npc.getNpcPath() != null;
      if (hasPath) {
        npc.getNpcPath().handle();
      }
      for (Player player : Bukkit.getOnlinePlayers()) {
        boolean canSeeNPC;
        ZUser zUser = ZUser.find(player);
        boolean bl2 =
            canSeeNPC =
                player.getWorld() == npc.getLocation().getWorld()
                    && player.getLocation().distance(npc.getLocation())
                        <= (double) ConfigurationConstants.VIEW_DISTANCE;
        if (npc.getViewers().contains(zUser) && !canSeeNPC) {
          npc.delete(zUser);
          continue;
        }
        if (!canSeeNPC) continue;
        if (!npc.getViewers().contains(zUser)) {
          npc.spawn(zUser);
        }
        if (FunctionFactory.isTrue(npc, "look") && !hasPath) {
          npc.lookAt(zUser, player.getLocation(), false);
        }
        npc.onUpdate(zUser);
        ConversationModel conversationStorage = npc.getNpcPojo().getConversation();
        if (conversationStorage == null
            || conversationStorage.getConversationType()
                != ConversationModel.ConversationType.RADIUS) continue;
        npc.tryStartConversation(player);
      }
    }
  }
}
