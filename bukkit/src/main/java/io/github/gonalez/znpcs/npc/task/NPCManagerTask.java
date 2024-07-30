package io.github.gonalez.znpcs.npc.task;

import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.ZNPConfigUtils;
import io.github.gonalez.znpcs.configuration.ConfigConfiguration;
import io.github.gonalez.znpcs.npc.FunctionFactory;
import io.github.gonalez.znpcs.npc.NPC;
import io.github.gonalez.znpcs.npc.conversation.ConversationModel;
import io.github.gonalez.znpcs.user.ZUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCManagerTask extends BukkitRunnable {
  public NPCManagerTask(ServersNPC serversNPC) {
    runTaskTimerAsynchronously(serversNPC, 60L, 1L);
  }
  
  public void run() {
    for (NPC npc : NPC.all()) {
      boolean hasPath = (npc.getNpcPath() != null);
      if (hasPath)
        npc.getNpcPath().handle(); 
      for (Player player : Bukkit.getOnlinePlayers()) {
        ZUser zUser = ZUser.find(player);
        boolean canSeeNPC = (player.getWorld() == npc.getLocation().getWorld()
            && player.getLocation().distance(npc.getLocation()) <= ZNPConfigUtils.getConfig(ConfigConfiguration.class).viewDistance);
        if (npc.getViewers().contains(zUser) && !canSeeNPC) {
          npc.delete(zUser);
          continue;
        } 
        if (canSeeNPC) {
          if (!npc.getViewers().contains(zUser))
            npc.spawn(zUser); 
          if (FunctionFactory.isTrue(npc, "look") && !hasPath)
            npc.lookAt(zUser, player.getLocation(), false); 
          npc.getHologram().updateNames(zUser);
          ConversationModel conversationStorage = npc.getNpcPojo().getConversation();
          if (conversationStorage != null && conversationStorage.getConversationType() == ConversationModel.ConversationType.RADIUS)
            npc.tryStartConversation(player); 
        } 
      } 
    } 
  }
}
