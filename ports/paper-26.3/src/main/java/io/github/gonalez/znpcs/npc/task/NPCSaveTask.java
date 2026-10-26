/*
 * ZNPCs 5.3 compatibility port for Paper 26.3.
 * Modified from, or added to, ZNPCs by Gaston Gonzalez.
 * Licensed under the Apache License 2.0; see LICENSE and NOTICE.
 */
package io.github.gonalez.znpcs.npc.task;

import io.github.gonalez.znpcs.ServersNPC;
import io.github.gonalez.znpcs.configuration.Configuration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCSaveTask extends BukkitRunnable {
  public NPCSaveTask(ServersNPC serversNPC, int seconds) {
    this.runTaskTimer((Plugin) serversNPC, seconds, seconds);
  }

  public void run() {
    Configuration.SAVE_CONFIGURATIONS.forEach(Configuration::save);
  }
}
